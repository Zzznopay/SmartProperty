package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.ParkingPaymentConverter;
import com.smart.property.property.domain.ParkingPayment;
import com.smart.property.property.dto.ParkingPaymentDTO;
import com.smart.property.property.mapper.ParkingPaymentMapper;
import com.smart.property.property.service.ParkingPaymentService;
import com.smart.property.property.vo.ParkingPaymentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 车位缴费服务实现
 *
 * @author zzz
 * @since 2026-07-28
 */
@Service
@RequiredArgsConstructor
public class ParkingPaymentServiceImpl extends ServiceImpl<ParkingPaymentMapper, ParkingPayment> implements ParkingPaymentService {

    private final ParkingPaymentConverter parkingPaymentConverter;

    @Override
    public PageResult<ParkingPaymentVO> getPage(PageQuery query, Long companyId, Long parkingId, Integer status) {
        Page<ParkingPayment> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<ParkingPayment> wrapper = new LambdaQueryWrapper<ParkingPayment>()
                .eq(ParkingPayment::getCompanyId, companyId)
                .eq(ParkingPayment::getIsDeleted, 0)
                .eq(parkingId != null, ParkingPayment::getParkingId, parkingId)
                .eq(status != null, ParkingPayment::getStatus, status)
                .orderByDesc(ParkingPayment::getPayTime);
        Page<ParkingPayment> result = baseMapper.selectPage(page, wrapper);
        List<ParkingPaymentVO> records = result.getRecords().stream()
                .map(parkingPaymentConverter::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public ParkingPaymentVO getParkingPaymentById(Long id, Long companyId) {
        return parkingPaymentConverter.toVO(getCompanyParkingPayment(id, companyId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collect(ParkingPaymentDTO dto, Long companyId, String operator) {
        ParkingPayment parkingPayment = parkingPaymentConverter.toEntity(dto);
        parkingPayment.setCompanyId(companyId);
        parkingPayment.setPaymentNo(generatePaymentNo());
        parkingPayment.setStatus(1);
        parkingPayment.setCreateBy(operator);
        save(parkingPayment);
    }

    @Override
    public void updateParkingPayment(Long id, ParkingPaymentDTO dto, Long companyId, String operator) {
        ParkingPayment existing = getCompanyParkingPayment(id, companyId);
        ParkingPayment patch = parkingPaymentConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setPaymentNo(existing.getPaymentNo());
        patch.setStatus(existing.getStatus());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    public void deleteParkingPayment(Long id, Long companyId) {
        getCompanyParkingPayment(id, companyId);
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long id, String remark, Long companyId, String operator) {
        ParkingPayment payment = getCompanyParkingPayment(id, companyId);
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
    public void voidPayment(Long id, String remark, Long companyId, String operator) {
        ParkingPayment payment = getCompanyParkingPayment(id, companyId);
        if (payment.getStatus() != 1) {
            throw new BusinessException("当前状态不允许作废");
        }
        payment.setStatus(3);
        payment.setRemark(remark);
        payment.setUpdateBy(operator);
        updateById(payment);
    }

    /**
     * 按公司隔离查询车位缴费记录，防止跨租户访问
     */
    private ParkingPayment getCompanyParkingPayment(Long id, Long companyId) {
        ParkingPayment payment = getOne(new LambdaQueryWrapper<ParkingPayment>()
                .eq(ParkingPayment::getId, id)
                .eq(ParkingPayment::getCompanyId, companyId));
        if (payment == null) {
            throw new BusinessException("车位缴费记录不存在");
        }
        return payment;
    }

    private String generatePaymentNo() {
        return "PARK" + System.currentTimeMillis();
    }
}
