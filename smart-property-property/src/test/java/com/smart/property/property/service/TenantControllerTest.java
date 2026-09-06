package com.smart.property.property.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Tenant;
import com.smart.property.property.vo.TenantVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

/**
 * 租户Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class TenantControllerTest {

    @Autowired
    private TenantService tenantService;

    @Test
    void testAddTenant() {
        Tenant tenant = new Tenant();
        tenant.setCompanyId(1L);
        tenant.setTenantCode("T001");
        tenant.setTenantName("张三");
        tenant.setGender(1);
        tenant.setPhone("13800138000");
        tenant.setPhoneMask("138****8000");
        tenant.setStatus(1);
        tenant.setCreateBy("test");

        tenant.setCreateTime(LocalDateTime.now());
        tenant.setUpdateTime(LocalDateTime.now());
        tenantService.save(tenant);

        assertNotNull(tenant.getId());
    }

    @Test
    void testGetTenantPage() {
        Tenant tenant = new Tenant();
        tenant.setCompanyId(1L);
        tenant.setTenantCode("TP001");
        tenant.setTenantName("李四");
        tenant.setStatus(1);
        tenant.setCreateTime(LocalDateTime.now());
        tenant.setUpdateTime(LocalDateTime.now());
        tenantService.save(tenant);

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<TenantVO> result = tenantService.getTenantPage(query, 1L);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testGetTenantById() {
        Tenant tenant = new Tenant();
        tenant.setCompanyId(1L);
        tenant.setTenantCode("TD001");
        tenant.setTenantName("王五");
        tenant.setStatus(1);
        tenant.setCreateTime(LocalDateTime.now());
        tenant.setUpdateTime(LocalDateTime.now());
        tenantService.save(tenant);

        TenantVO result = tenantService.getTenantById(tenant.getId(), 1L);

        assertNotNull(result);
        assertEquals("王五", result.getTenantName());
    }

    @Test
    void testUpdateTenant() {
        Tenant tenant = new Tenant();
        tenant.setCompanyId(1L);
        tenant.setTenantCode("TU001");
        tenant.setTenantName("更新前");
        tenant.setStatus(1);
        tenant.setCreateTime(LocalDateTime.now());
        tenant.setUpdateTime(LocalDateTime.now());
        tenantService.save(tenant);

        tenant.setTenantName("更新后");
        tenant.setUpdateBy("test");
        tenant.setUpdateTime(java.time.LocalDateTime.now());
        tenantService.updateById(tenant);

        Tenant updated = tenantService.getById(tenant.getId());
        assertEquals("更新后", updated.getTenantName());
    }

    @Test
    void testDeleteTenant() {
        Tenant tenant = new Tenant();
        tenant.setCompanyId(1L);
        tenant.setTenantCode("TDEL001");
        tenant.setTenantName("删除测试");
        tenant.setStatus(1);
        tenant.setCreateTime(LocalDateTime.now());
        tenant.setUpdateTime(LocalDateTime.now());
        tenantService.save(tenant);

        tenant.setUpdateTime(LocalDateTime.now());
        tenantService.removeById(tenant);

        Tenant deleted = tenantService.getById(tenant.getId());
        assertNull(deleted);
    }
}
