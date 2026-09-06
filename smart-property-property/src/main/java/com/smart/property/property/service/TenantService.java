package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Tenant;
import com.smart.property.property.dto.TenantDTO;
import com.smart.property.property.vo.TenantVO;

/**
 * 租户服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface TenantService extends IService<Tenant> {

    PageResult<TenantVO> getTenantPage(PageQuery query, Long companyId);

    TenantVO getTenantById(Long id, Long companyId);

    void createTenant(TenantDTO dto, Long companyId, String operator);

    void updateTenant(Long id, TenantDTO dto, Long companyId, String operator);

    void deleteTenant(Long id, Long companyId);
}