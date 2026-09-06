package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.RentPaymentConverter;
import com.smart.property.property.domain.RentPayment;
import com.smart.property.property.dto.RentPaymentDTO;
import com.smart.property.property.mapper.RentPaymentMapper;
import com.smart.property.property.service.RentPaymentService;
import com.smart.property.property.vo.RentPaymentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 租金收取服务实现
 *
 * @author zzz
 * @since 2026-07-28
 */
@Service
@RequiredArgsConstructor
public class RentPaymentServiceImpl extends ServiceImpl<RentPaymentMapper, RentPayment> implements RentPaymentService {

    private final RentPaymentConverter rentPaymentConverter;

    @Override
    public PageResult<RentPaymentVO> getRentPaymentPage(PageQuery query, Long companyId, Long contractId, Integer status) {
        Page<RentPayment> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<RentPayment> wrapper = new LambdaQueryWrapper<RentPayment>()
                .eq(RentPayment::getCompanyId, companyId)
                .eq(RentPayment::getIsDeleted, 0)
                .eq(contractId != null, RentPayment::getContractId, contractId)
                .eq(status != null, RentPayment::getStatus, status)
                .orderByDesc(RentPayment::getPayTime);
        Page<RentPayment> result = baseMapper.selectPage(page, wrapper);
        List<RentPaymentVO> records = result.getRecords().stream()
                .map(rentPaymentConverter::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public RentPaymentVO getRentPaymentById(Long id, Long companyId) {
        return rentPaymentConverter.toVO(getCompanyRentPayment(id, companyId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collectRent(RentPaymentDTO dto, Long companyId, String operator) {
        RentPayment rentPayment = rentPaymentConverter.toEntity(dto);
        rentPayment.setCompanyId(companyId);
        rentPayment.setPaymentNo(generatePaymentNo());
        rentPayment.setStatus(1);
        rentPayment.setCreateBy(operator);
        save(rentPayment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundRent(Long id, Long companyId, String remark, String operator) {
        RentPayment payment = getCompanyRentPayment(id, companyId);
        if (payment.getStatus() != 1) {
            throw new BusinessException("当前状态不允许退款");
        }
        payment.setStatus(2);
        payment.setRemark(remark);
        payment.setUpdateBy(operator);
        updateById(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void voidRent(Long id, Long companyId, String remark, String operator) {
        RentPayment payment = getCompanyRentPayment(id, companyId);
        if (payment.getStatus() != 1) {
            throw new BusinessException("当前状态不允许作废");
        }
        payment.setStatus(3);
        payment.setRemark(remark);
        payment.setUpdateBy(operator);
        updateById(payment);
    }

    private RentPayment getCompanyRentPayment(Long id, Long companyId) {
        RentPayment payment = getOne(new LambdaQueryWrapper<RentPayment>()
                .eq(RentPayment::getId, id)
                .eq(RentPayment::getCompanyId, companyId));
        if (payment == null) {
            throw new BusinessException("租金记录不存在");
        }
        return payment;
    }

    private String generatePaymentNo() {
        return "RENT" + System.currentTimeMillis();
    }
}