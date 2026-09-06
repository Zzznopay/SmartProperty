package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.PrepaymentConverter;
import com.smart.property.property.convert.PrepaymentUsageConverter;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.domain.Prepayment;
import com.smart.property.property.domain.PrepaymentUsage;
import com.smart.property.property.dto.PrepaymentDTO;
import com.smart.property.property.mapper.OwnerMapper;
import com.smart.property.property.mapper.PrepaymentMapper;
import com.smart.property.property.mapper.PrepaymentUsageMapper;
import com.smart.property.property.service.PrepaymentService;
import com.smart.property.property.vo.PrepaymentUsageVO;
import com.smart.property.property.vo.PrepaymentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 预收款服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class PrepaymentServiceImpl extends ServiceImpl<PrepaymentMapper, Prepayment> implements PrepaymentService {

    private final PrepaymentUsageMapper prepaymentUsageMapper;
    private final PrepaymentConverter prepaymentConverter;
    private final PrepaymentUsageConverter prepaymentUsageConverter;
    private final OwnerMapper ownerMapper;

    /** 批量回填 VO 的业主名称（列表接口 VO 只带 ownerId） */
    private void fillOwnerNames(List<PrepaymentVO> records) {
        if (records.isEmpty()) {
            return;
        }
        List<Long> ownerIds = records.stream()
                .map(PrepaymentVO::getOwnerId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (ownerIds.isEmpty()) {
            return;
        }
        Map<Long, String> nameMap = ownerMapper.selectList(
                        new LambdaQueryWrapper<Owner>().in(Owner::getId, ownerIds))
                .stream()
                .collect(Collectors.toMap(Owner::getId, Owner::getOwnerName, (a, b) -> a));
        records.forEach(vo -> vo.setOwnerName(nameMap.get(vo.getOwnerId())));
    }

    @Override
    public PrepaymentVO getById(Long id, Long companyId) {
        PrepaymentVO vo = prepaymentConverter.toVO(getCompanyPrepayment(id, companyId));
        fillOwnerNames(List.of(vo));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePrepayment(Long id, PrepaymentDTO dto, Long companyId, String operator) {
        Prepayment existing = getCompanyPrepayment(id, companyId);
        Prepayment patch = prepaymentConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    public List<PrepaymentUsageVO> getUsages(Long prepaymentId, Long companyId) {
        List<PrepaymentUsage> records = prepaymentUsageMapper.selectList(
                new LambdaQueryWrapper<PrepaymentUsage>()
                        .eq(PrepaymentUsage::getPrepaymentId, prepaymentId)
                        .eq(PrepaymentUsage::getCompanyId, companyId)
                        .orderByDesc(PrepaymentUsage::getUseTime)
        );
        return prepaymentUsageConverter.toVOList(records);
    }

    @Override
    public PageResult<PrepaymentVO> getPrepaymentPage(PageQuery query, Long companyId, Long ownerId) {
        Page<Prepayment> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Prepayment> wrapper = new LambdaQueryWrapper<Prepayment>()
                .eq(Prepayment::getCompanyId, companyId)
                .eq(Prepayment::getIsDeleted, 0)
                .eq(ownerId != null, Prepayment::getOwnerId, ownerId)
                .orderByDesc(Prepayment::getCreateTime);

        Page<Prepayment> result = baseMapper.selectPage(page, wrapper);
        List<PrepaymentVO> records = result.getRecords().stream()
                .map(prepaymentConverter::toVO)
                .collect(Collectors.toList());
        fillOwnerNames(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPrepayment(PrepaymentDTO dto, Long companyId, String operator) {
        Prepayment prepayment = prepaymentConverter.toEntity(dto);
        prepayment.setCompanyId(companyId);
        prepayment.setUsedAmount(BigDecimal.ZERO);
        prepayment.setBalance(prepayment.getAmount());
        prepayment.setPayTime(LocalDateTime.now());
        prepayment.setStatus(1);
        prepayment.setPaymentNo(generatePaymentNo());
        prepayment.setCreateBy(operator);
        baseMapper.insert(prepayment);
    }

    @Override
    public BigDecimal getBalance(Long ownerId, Long companyId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<Prepayment>()
                        .eq(Prepayment::getOwnerId, ownerId)
                        .eq(Prepayment::getCompanyId, companyId)
                        .eq(Prepayment::getStatus, 1)
                        .eq(Prepayment::getIsDeleted, 0)
        ).stream()
                .map(Prepayment::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundPrepayment(Long id, Long companyId, String operator) {
        Prepayment prepayment = getCompanyPrepayment(id, companyId);
        if (prepayment.getStatus() != 1) {
            throw new BusinessException("当前状态不允许退款");
        }
        prepayment.setStatus(2);
        prepayment.setUpdateBy(operator);
        baseMapper.updateById(prepayment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePrepayment(Long id, Long companyId) {
        getCompanyPrepayment(id, companyId);
        removeById(id);
    }

    private Prepayment getCompanyPrepayment(Long id, Long companyId) {
        Prepayment prepayment = getOne(new LambdaQueryWrapper<Prepayment>()
                .eq(Prepayment::getId, id)
                .eq(Prepayment::getCompanyId, companyId));
        if (prepayment == null) {
            throw new BusinessException("预收款记录不存在");
        }
        return prepayment;
    }

    private String generatePaymentNo() {
        return "PRE" + System.currentTimeMillis();
    }
}