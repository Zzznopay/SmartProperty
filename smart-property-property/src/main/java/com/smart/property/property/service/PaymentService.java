package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.property.domain.Payment;
import com.smart.property.property.dto.PaymentDTO;
import com.smart.property.property.dto.PaymentDetailDTO;
import com.smart.property.property.vo.PaymentDetailVO;
import com.smart.property.property.vo.PaymentVO;

import java.util.List;

/**
 * 收费记录服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface PaymentService extends IService<Payment> {

    /**
     * 分页查询收费记录
     */
    PageResult<PaymentVO> getPaymentPage(PageQuery query, Long companyId, Long roomId, Long ownerId);

    /**
     * 详情
     */
    PaymentVO getPaymentById(Long id);

    /**
     * 收费明细列表
     */
    List<PaymentDetailVO> getPaymentDetails(Long paymentId);

    /**
     * 收取物业费（批量明细）
     */
    void collectPayment(PaymentDTO dto, List<PaymentDetailDTO> details, Long companyId, String operator);

    /**
     * 收取物业费（无明细）
     */
    void collectPayment(PaymentDTO dto, Long companyId, String operator);

    /**
     * 退款
     */
    void refundPayment(Long id, String remark, String operator);

    /**
     * 作废
     */
    void voidPayment(Long id, String remark, String operator);

    /**
     * 审核：audit_status 0->1
     */
    void auditPayment(Long id, String operator);
}
