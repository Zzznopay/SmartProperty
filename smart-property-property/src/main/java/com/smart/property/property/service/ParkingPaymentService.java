package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.ParkingPayment;
import com.smart.property.property.dto.ParkingPaymentDTO;
import com.smart.property.property.vo.ParkingPaymentVO;

/**
 * 车位缴费服务接口
 *
 * @author zzz
 * @since 2026-07-28
 */
public interface ParkingPaymentService extends IService<ParkingPayment> {

    /**
     * 分页查询车位缴费记录
     */
    PageResult<ParkingPaymentVO> getPage(PageQuery query, Long companyId, Long parkingId, Integer status);

    /**
     * 详情
     */
    ParkingPaymentVO getParkingPaymentById(Long id, Long companyId);

    /**
     * 创建缴费记录
     */
    void collect(ParkingPaymentDTO dto, Long companyId, String operator);

    /**
     * 按公司隔离更新车位缴费记录
     */
    void updateParkingPayment(Long id, ParkingPaymentDTO dto, Long companyId, String operator);

    /**
     * 按公司隔离逻辑删除车位缴费记录
     */
    void deleteParkingPayment(Long id, Long companyId);

    /**
     * 退款
     */
    void refund(Long id, String remark, Long companyId, String operator);

    /**
     * 作废
     */
    void voidPayment(Long id, String remark, Long companyId, String operator);
}
