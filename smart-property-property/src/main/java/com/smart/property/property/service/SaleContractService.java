package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.SaleContract;
import com.smart.property.property.dto.SaleContractDTO;
import com.smart.property.property.vo.SaleContractVO;

/**
 * 销售合同服务接口
 *
 * @author zzz
 * @since 2026-07-28
 */
public interface SaleContractService extends IService<SaleContract> {

    PageResult<SaleContractVO> getSaleContractPage(PageQuery query, Long companyId, Long roomId, Integer status);

    SaleContractVO getByContractId(Long id, Long companyId);

    void createSaleContract(SaleContractDTO dto, Long companyId, String operator);

    void updateSaleContract(Long id, SaleContractDTO dto, Long companyId, String operator);

    void deliver(Long id, Long companyId, String operator);

    void deleteSaleContract(Long id, Long companyId);
}