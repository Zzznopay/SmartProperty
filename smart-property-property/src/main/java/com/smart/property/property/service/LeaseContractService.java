package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.LeaseContract;
import com.smart.property.property.dto.LeaseContractDTO;
import com.smart.property.property.vo.LeaseContractVO;

/**
 * 租赁合同服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface LeaseContractService extends IService<LeaseContract> {

    PageResult<LeaseContractVO> getContractPage(PageQuery query, Long companyId, Integer status);

    LeaseContractVO getContractById(Long id, Long companyId);

    void createContract(LeaseContractDTO dto, Long companyId, String operator);

    void updateContract(Long id, LeaseContractDTO dto, Long companyId, String operator);

    void terminateContract(Long id, Long companyId, String reason, String operator);

    PageResult<LeaseContractVO> getExpiringContracts(PageQuery query, Long companyId, int days);

    void activateContract(Long id, Long companyId, String operator);

    void transferContract(Long id, Long companyId, Long newTenantId, String operator);

    void deleteContract(Long id, Long companyId);
}