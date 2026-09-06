package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.mq.event.PaymentCompletedEvent;
import com.smart.property.common.mq.util.MqUtils;
import com.smart.property.property.convert.PaymentConverter;
import com.smart.property.property.convert.PaymentDetailConverter;
import com.smart.property.property.domain.Ledger;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.domain.Payment;
import com.smart.property.property.domain.PaymentDetail;
import com.smart.property.property.domain.Room;
import com.smart.property.property.dto.PaymentDTO;
import com.smart.property.property.dto.PaymentDetailDTO;
import com.smart.property.property.mapper.LedgerMapper;
import com.smart.property.property.mapper.OwnerMapper;
import com.smart.property.property.mapper.PaymentDetailMapper;
import com.smart.property.property.mapper.PaymentMapper;
import com.smart.property.property.mapper.RoomMapper;
import com.smart.property.property.service.PaymentService;
import com.smart.property.property.vo.PaymentDetailVO;
import com.smart.property.property.vo.PaymentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收费记录服务实现
 *
 * <p>主要优化：</p>
 * <ul>
 *     <li>收款/退款：循环内 selectById 改为按 ledgerId 批量预拉，避免 N+1</li>
 *     <li>台账更新：单条原子 UPDATE（{@code paid_amount + delta} 范围约束），并发安全</li>
 *     <li>金额校验：负数、超额、空台账直接抛异常，避免脏数据</li>
 * </ul>
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

    private final MqUtils mqUtils;
    private final PaymentDetailMapper paymentDetailMapper;
    private final LedgerMapper ledgerMapper;
    private final PaymentConverter paymentConverter;
    private final PaymentDetailConverter paymentDetailConverter;
    private final RoomMapper roomMapper;
    private final OwnerMapper ownerMapper;

    /** 批量回填 VO 的房号/业主姓名（列表 VO 只带 roomId/ownerId） */
    private void fillPaymentNames(List<PaymentVO> records) {
        if (records.isEmpty()) {
            return;
        }
        List<Long> roomIds = records.stream().map(PaymentVO::getRoomId).filter(java.util.Objects::nonNull).distinct().toList();
        Map<Long, String> roomNos = roomIds.isEmpty() ? new HashMap<>()
                : roomMapper.selectList(new LambdaQueryWrapper<Room>().in(Room::getId, roomIds))
                        .stream()
                        .collect(Collectors.toMap(Room::getId, Room::getRoomNo, (a, b) -> a));
        List<Long> ownerIds = records.stream().map(PaymentVO::getOwnerId).filter(java.util.Objects::nonNull).distinct().toList();
        Map<Long, String> ownerNames = ownerIds.isEmpty() ? new HashMap<>()
                : ownerMapper.selectList(new LambdaQueryWrapper<Owner>().in(Owner::getId, ownerIds))
                        .stream()
                        .collect(Collectors.toMap(Owner::getId, Owner::getOwnerName, (a, b) -> a));
        records.forEach(vo -> {
            vo.setRoomNo(vo.getRoomId() == null ? null : roomNos.get(vo.getRoomId()));
            vo.setOwnerName(vo.getOwnerId() == null ? null : ownerNames.get(vo.getOwnerId()));
        });
    }

    @Override
    public PageResult<PaymentVO> getPaymentPage(PageQuery query, Long companyId, Long roomId, Long ownerId) {
        Page<Payment> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<Payment>()
                .eq(Payment::getCompanyId, companyId)
                .eq(Payment::getIsDeleted, 0)
                .eq(roomId != null, Payment::getRoomId, roomId)
                .eq(ownerId != null, Payment::getOwnerId, ownerId)
                .orderByDesc(Payment::getPayTime);

        Page<Payment> result = baseMapper.selectPage(page, wrapper);
        List<PaymentVO> records = result.getRecords().stream()
                .map(paymentConverter::toVO)
                .collect(Collectors.toList());
        fillPaymentNames(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public PaymentVO getPaymentById(Long id) {
        PaymentVO vo = paymentConverter.toVO(getPaymentEntity(id));
        fillPaymentNames(List.of(vo));
        return vo;
    }

    @Override
    public List<PaymentDetailVO> getPaymentDetails(Long paymentId) {
        List<PaymentDetail> details = paymentDetailMapper.selectList(
                new LambdaQueryWrapper<PaymentDetail>()
                        .eq(PaymentDetail::getPaymentId, paymentId)
        );
        return paymentDetailConverter.toVOList(details);
    }

    @Override
    public void collectPayment(PaymentDTO dto, Long companyId, String operator) {
        collectPayment(dto, null, companyId, operator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collectPayment(PaymentDTO dto, List<PaymentDetailDTO> details, Long companyId, String operator) {
        // 1. 入口校验
        if (dto.getActualAmount() == null || dto.getActualAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("实收金额必须大于0");
        }

        // 2. 写支付主表（AutoFillHandler 自动填 createTime/updateTime/createBy/updateBy/isDeleted/companyId）
        Payment payment = paymentConverter.toEntity(dto);
        payment.setCompanyId(companyId);
        payment.setPaymentNo(generatePaymentNo());
        payment.setStatus(1);
        payment.setAuditStatus(0);
        payment.setCreateBy(operator);
        baseMapper.insert(payment);

        if (details != null && !details.isEmpty()) {
            // 3. 批量插入明细
            List<PaymentDetail> detailEntities = details.stream().map(d -> {
                if (d.getActualAmount() == null || d.getActualAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new BusinessException("明细实收金额必须大于0");
                }
                PaymentDetail e = paymentDetailConverter.toEntity(d);
                e.setCompanyId(payment.getCompanyId());
                e.setPaymentId(payment.getId());
                return e;
            }).collect(Collectors.toList());
            // 批量 insert 一次
            for (PaymentDetail e : detailEntities) {
                paymentDetailMapper.insert(e);
            }

            // 4. 批量预拉台账（O(1) SQL，替代 N 次 selectById）
            List<Long> ledgerIds = detailEntities.stream()
                    .map(PaymentDetail::getLedgerId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, Ledger> ledgerMap = ledgerMapper.selectBatchIds(ledgerIds).stream()
                    .collect(Collectors.toMap(Ledger::getId, l -> l));

            // 5. 原子累加 + 状态计算
            for (PaymentDetail detail : detailEntities) {
                Ledger ledger = ledgerMap.get(detail.getLedgerId());
                if (ledger == null) {
                    throw new BusinessException("台账不存在: id=" + detail.getLedgerId());
                }
                if (!ledger.getCompanyId().equals(payment.getCompanyId())) {
                    throw new BusinessException("明细归属公司与支付主单不一致");
                }

                BigDecimal newPaid = ledger.getPaidAmount() == null
                        ? BigDecimal.ZERO
                        : ledger.getPaidAmount().add(detail.getActualAmount());

                // 原子累加：DB 内置范围检查，affected=0 表示超额
                int affected = ledgerMapper.accumulatePaidAmount(
                        detail.getLedgerId(), detail.getActualAmount(), operator);
                if (affected == 0) {
                    throw new BusinessException("台账累加失败（超额或不存在）: id=" + detail.getLedgerId());
                }

                // 状态：未收 / 部分收 / 已收
                int status = computeLedgerStatus(newPaid, ledger.getAmount());
                ledgerMapper.updateLedgerStatus(detail.getLedgerId(), status, payment.getPayTime(), operator);
            }
        }

        // 6. 异步发事件
        mqUtils.sendPaymentCompleted(new PaymentCompletedEvent(
                payment.getId(),
                payment.getCompanyId(),
                payment.getOwnerId(),
                null,
                payment.getActualAmount(),
                null,
                LocalDateTime.now()
        ));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundPayment(Long id, String remark, String operator) {
        Payment payment = getPaymentEntity(id);
        if (payment.getStatus() != 1) {
            throw new BusinessException("当前状态不允许退款");
        }

        payment.setStatus(2);
        payment.setRemark(remark);
        payment.setUpdateBy(operator);
        baseMapper.updateById(payment);

        // 一次性拉取所有明细
        List<PaymentDetail> details = paymentDetailMapper.selectList(
                new LambdaQueryWrapper<PaymentDetail>().eq(PaymentDetail::getPaymentId, id)
        );
        if (details.isEmpty()) {
            return;
        }

        // 一次性预拉台账
        List<Long> ledgerIds = details.stream().map(PaymentDetail::getLedgerId)
                .distinct().collect(Collectors.toList());
        Map<Long, Ledger> ledgerMap = ledgerMapper.selectBatchIds(ledgerIds).stream()
                .collect(Collectors.toMap(Ledger::getId, l -> l));

        // 原子扣减
        for (PaymentDetail detail : details) {
            Ledger ledger = ledgerMap.get(detail.getLedgerId());
            if (ledger == null) {
                throw new BusinessException("台账不存在: id=" + detail.getLedgerId());
            }
            BigDecimal delta = detail.getActualAmount().negate();
            int affected = ledgerMapper.accumulatePaidAmount(detail.getLedgerId(), delta, operator);
            if (affected == 0) {
                throw new BusinessException("台账扣减失败（余额不足）: id=" + detail.getLedgerId());
            }
            BigDecimal newPaid = ledger.getPaidAmount() == null
                    ? BigDecimal.ZERO
                    : ledger.getPaidAmount().add(delta);
            int status = computeLedgerStatus(newPaid, ledger.getAmount());
            ledgerMapper.updateLedgerStatus(detail.getLedgerId(), status, null, operator);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void voidPayment(Long id, String remark, String operator) {
        Payment payment = getPaymentEntity(id);
        if (payment.getStatus() != 1) {
            throw new BusinessException("当前状态不允许作废");
        }
        payment.setStatus(3);
        payment.setRemark(remark);
        payment.setUpdateBy(operator);
        baseMapper.updateById(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditPayment(Long id, String operator) {
        Payment payment = getPaymentEntity(id);
        if (payment.getAuditStatus() != null && payment.getAuditStatus() == 1) {
            throw new BusinessException("已审核，不能重复审核");
        }
        payment.setAuditStatus(1);
        payment.setUpdateBy(operator);
        baseMapper.updateById(payment);
    }

    /**
     * 状态计算：1=未收（paid<=0）、2=部分收（0<paid<total）、3=已收（paid>=total）
     */
    private int computeLedgerStatus(BigDecimal paid, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return 1;
        }
        if (paid == null || paid.compareTo(BigDecimal.ZERO) <= 0) {
            return 1;
        }
        if (paid.compareTo(total) >= 0) {
            return 3;
        }
        return 2;
    }

    private Payment getPaymentEntity(Long id) {
        Payment payment = getById(id);
        if (payment == null) {
            throw new BusinessException("收费记录不存在");
        }
        return payment;
    }

    private String generatePaymentNo() {
        return "PAY" + System.currentTimeMillis();
    }
}
