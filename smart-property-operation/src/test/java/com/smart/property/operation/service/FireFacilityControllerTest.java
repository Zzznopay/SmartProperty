package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.FireFacility;
import com.smart.property.operation.vo.FireFacilityVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

/**
 * 消防设施Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class FireFacilityControllerTest {

    @Autowired
    private FireFacilityService fireFacilityService;

    @Test
    void testAddFireFacility() {
        FireFacility facility = new FireFacility();
        facility.setCompanyId(1L);
        facility.setCommunityId(1L);
        facility.setFacilityName("灭火器");
        facility.setFacilityType(1);
        facility.setLocation("1楼");
        facility.setStatus(1);
        facility.setCreateBy("test");

        facility.setCreateTime(java.time.LocalDateTime.now());
        facility.setUpdateTime(java.time.LocalDateTime.now());
        fireFacilityService.save(facility);

        assertNotNull(facility.getId());
    }

    @Test
    void testGetFireFacilityPage() {
        FireFacility facility = new FireFacility();
        facility.setCompanyId(1L);
        facility.setCommunityId(1L);
        facility.setFacilityName("分页测试");
        facility.setFacilityType(1);
        facility.setStatus(1);
        facility.setCreateTime(java.time.LocalDateTime.now());
        facility.setUpdateTime(java.time.LocalDateTime.now());
        fireFacilityService.save(facility);

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<FireFacilityVO> result = fireFacilityService.getFireFacilityPage(query, 1L, null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testCheckFacility() {
        FireFacility facility = new FireFacility();
        facility.setCompanyId(1L);
        facility.setCommunityId(1L);
        facility.setFacilityName("检查测试");
        facility.setFacilityType(1);
        facility.setStatus(1);
        facility.setCreateTime(java.time.LocalDateTime.now());
        facility.setUpdateTime(java.time.LocalDateTime.now());
        fireFacilityService.save(facility);

        fireFacilityService.checkFacility(facility.getId(), "test");

        FireFacility checked = fireFacilityService.getById(facility.getId());
        assertNotNull(checked.getLastCheckDate());
    }
}
