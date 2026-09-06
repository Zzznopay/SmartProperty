package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Invoice;
import com.smart.property.property.dto.InvoiceDTO;
import com.smart.property.property.vo.InvoiceVO;

import java.util.List;

/**
 * 票据服务接口
 *
 * @author zzz
 * @since 2026-07-28
 */
public interface InvoiceService extends IService<Invoice> {

    PageResult<InvoiceVO> getInvoicePage(PageQuery query, Long companyId, Integer status, Integer invoiceType);

    int assignInvoices(List<Long> invoiceIds, Long userId, String userName, Long companyId, String operator);

    void applyVoid(Long id, String reason, Long companyId, String operator);

    void confirmVoid(Long id, Long companyId, String operator);

    void deleteInvoice(Long id, Long companyId);

    void importInvoices(List<InvoiceDTO> invoices, Long companyId, String operator);
}