package com.smart.property.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.property.property.domain.PaymentDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
 * 缴费明细 Mapper
 *
 * @author zzz
 * @since 2026-07-28
 */
@Mapper
public interface PaymentDetailMapper extends BaseMapper<PaymentDetail> {

    /**
     * 批量按 paymentId 拉取明细（避免循环内 selectList）
     */
    @Select("<script>" +
            "SELECT * FROM payment_detail WHERE is_deleted = 0 AND payment_id IN " +
            "<foreach collection='paymentIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<PaymentDetail> selectByPaymentIds(@Param("paymentIds") Collection<Long> paymentIds);

    /**
     * 批量按 ledgerId 拉取明细
     */
    @Select("<script>" +
            "SELECT * FROM payment_detail WHERE is_deleted = 0 AND ledger_id IN " +
            "<foreach collection='ledgerIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<PaymentDetail> selectByLedgerIds(@Param("ledgerIds") Collection<Long> ledgerIds);
}
