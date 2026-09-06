package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.InvoiceConverter;
import com.smart.property.property.domain.Invoice;
import com.smart.property.property.dto.InvoiceDTO;
import com.smart.property.property.mapper.InvoiceMapper;
import com.smart.property.property.service.InvoiceService;
import com.smart.property.property.vo.InvoiceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 票据服务实现
 *
 * @author zzz
 * @since 2026-07-28
 */
@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl extends ServiceImpl<InvoiceMapper, Invoice> implements InvoiceService {

    private final InvoiceConverter invoiceConverter;

    @Override
    public PageResult<InvoiceVO> getInvoicePage(PageQuery query, Long companyId, Integer status, Integer invoiceType) {
        Page<Invoice> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Invoice> wrapper = new LambdaQueryWrapper<Invoice>()
                .eq(Invoice::getCompanyId, companyId)
                .eq(Invoice::getIsDeleted, 0)
                .eq(status != null, Invoice::getStatus, status)
                .eq(invoiceType != null, Invoice::getInvoiceType, invoiceType)
                .orderByDesc(Invoice::getCreateTime);
        Page<Invoice> result = baseMapper.selectPage(page, wrapper);
        List<InvoiceVO> records = result.getRecords().stream()
                .map(invoiceConverter::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int assignInvoices(List<Long> invoiceIds, Long userId, String userName, Long companyId, String operator) {
        if (invoiceIds == null || invoiceIds.isEmpty()) {
            return 0;
        }
        Invoice update = new Invoice();
        update.setUserId(userId);
        update.setUserName(userName);
        update.setUpdateBy(operator);
        return baseMapper.update(update,
                new LambdaQueryWrapper<Invoice>()
                        .in(Invoice::getId, invoiceIds)
                        .eq(Invoice::getCompanyId, companyId)
                        .eq(Invoice::getStatus, 1)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyVoid(Long id, String reason, Long companyId, String operator) {
        Invoice invoice = getCompanyInvoice(id, companyId);
        if (invoice.getStatus() == 3) {
            throw new BusinessException("票据已作废");
        }
        invoice.setVoidReason(reason);
        invoice.setUpdateBy(operator);
        baseMapper.updateById(invoice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmVoid(Long id, Long companyId, String operator) {
        Invoice invoice = getCompanyInvoice(id, companyId);
        if (invoice.getStatus() == 3) {
            return;
        }
        invoice.setStatus(3);
        invoice.setVoidTime(LocalDateTime.now());
        invoice.setUpdateBy(operator);
        baseMapper.updateById(invoice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInvoice(Long id, Long companyId) {
        Invoice invoice = getCompanyInvoice(id, companyId);
        if (invoice.getStatus() == 2) {
            throw new BusinessException("已使用的票据不允许删除");
        }
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importInvoices(List<InvoiceDTO> dtos, Long companyId, String operator) {
        if (dtos == null || dtos.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<Invoice> entities = new ArrayList<>(dtos.size());
        for (int i = 0; i < dtos.size(); i++) {
            InvoiceDTO dto = dtos.get(i);
            if (dto.getInvoiceType() == null) {
                throw new BusinessException("票据类型不能为空");
            }
            Invoice invoice = invoiceConverter.toEntity(dto);
            invoice.setCompanyId(companyId);
            if (invoice.getInvoiceNo() == null || invoice.getInvoiceNo().isBlank()) {
                invoice.setInvoiceNo(generateInvoiceNo(i));
            }
            invoice.setStatus(1);
            invoice.setCreateBy(operator);
            entities.add(invoice);
        }
        try {
            saveBatch(entities);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("票据编号重复，导入失败");
        }
    }

    private Invoice getCompanyInvoice(Long id, Long companyId) {
        Invoice invoice = getOne(new LambdaQueryWrapper<Invoice>()
                .eq(Invoice::getId, id)
                .eq(Invoice::getCompanyId, companyId));
        if (invoice == null) {
            throw new BusinessException("票据不存在");
        }
        return invoice;
    }

    private String generateInvoiceNo(int seq) {
        return "INV" + System.currentTimeMillis() + String.format("%03d", seq);
    }
}