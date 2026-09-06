package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Prepayment;
import com.smart.property.property.dto.PrepaymentDTO;
import com.smart.property.property.vo.PrepaymentUsageVO;
import com.smart.property.property.vo.PrepaymentVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 预收款服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface PrepaymentService extends IService<Prepayment> {

    PageResult<PrepaymentVO> getPrepaymentPage(PageQuery query, Long companyId, Long ownerId);

    void createPrepayment(PrepaymentDTO dto, Long companyId, String operator);

    BigDecimal getBalance(Long ownerId, Long companyId);

    void refundPrepayment(Long id, Long companyId, String operator);

    void updatePrepayment(Long id, PrepaymentDTO dto, Long companyId, String operator);

    PrepaymentVO getById(Long id, Long companyId);

    List<PrepaymentUsageVO> getUsages(Long prepaymentId, Long companyId);

    void deletePrepayment(Long id, Long companyId);
}