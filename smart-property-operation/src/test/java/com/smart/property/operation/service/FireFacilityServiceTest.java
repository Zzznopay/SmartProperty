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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 消防设施服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class FireFacilityServiceTest {

    @Autowired
    private FireFacilityService fireFacilityService;

    @Test
    void testSaveFireFacility() {
        FireFacility facility = new FireFacility();
        facility.setCompanyId(1L);
        facility.setCommunityId(1L);
        facility.setFacilityName("灭火器A");
        facility.setFacilityType(1);
        facility.setFacilityNo("F001");
        facility.setLocation("1号楼大厅");
        facility.setInstallDate(LocalDate.now().minusYears(1));
        facility.setExpireDate(LocalDate.now().plusYears(2));
        facility.setStatus(1);
        facility.setCreateBy("test");
        facility.setCreateTime(LocalDateTime.now());
        facility.setUpdateTime(LocalDateTime.now());

        boolean saved = fireFacilityService.save(facility);

        assertTrue(saved);
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
        facility.setCreateTime(LocalDateTime.now());
        facility.setUpdateTime(LocalDateTime.now());
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
        facility.setCreateTime(LocalDateTime.now());
        facility.setUpdateTime(LocalDateTime.now());
        fireFacilityService.save(facility);

        fireFacilityService.checkFacility(facility.getId(), "test");

        FireFacility checked = fireFacilityService.getById(facility.getId());
        assertNotNull(checked.getLastCheckDate());
        assertNotNull(checked.getNextCheckDate());
    }
}
