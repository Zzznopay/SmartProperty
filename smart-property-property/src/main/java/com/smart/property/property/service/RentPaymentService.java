package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.RentPayment;
import com.smart.property.property.dto.RentPaymentDTO;
import com.smart.property.property.vo.RentPaymentVO;

/**
 * 租金收取服务接口
 *
 * @author zzz
 * @since 2026-07-28
 */
public interface RentPaymentService extends IService<RentPayment> {

    PageResult<RentPaymentVO> getRentPaymentPage(PageQuery query, Long companyId, Long contractId, Integer status);

    RentPaymentVO getRentPaymentById(Long id, Long companyId);

    void collectRent(RentPaymentDTO dto, Long companyId, String operator);

    void refundRent(Long id, Long companyId, String remark, String operator);

    void voidRent(Long id, Long companyId, String remark, String operator);
}