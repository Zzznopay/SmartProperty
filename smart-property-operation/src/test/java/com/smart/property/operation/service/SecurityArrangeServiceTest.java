package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.SecurityArrange;
import com.smart.property.operation.vo.SecurityArrangeVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 保安安排服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class SecurityArrangeServiceTest {

    @Autowired
    private SecurityArrangeService securityArrangeService;

    @Test
    void testSaveSecurityArrange() {
        SecurityArrange arrange = new SecurityArrange();
        arrange.setCompanyId(1L);
        arrange.setCommunityId(1L);
        arrange.setArrangeDate(LocalDate.now());
        arrange.setShiftType(1);
        arrange.setStartTime(LocalTime.of(8, 0));
        arrange.setEndTime(LocalTime.of(16, 0));
        arrange.setPosition("大门");
        arrange.setSecurityId(1L);
        arrange.setSecurityName("保安A");
        arrange.setStatus(1);
        arrange.setCreateBy("test");
        arrange.setCreateTime(LocalDateTime.now());
        arrange.setUpdateTime(LocalDateTime.now());

        boolean saved = securityArrangeService.save(arrange);

        assertTrue(saved);
        assertNotNull(arrange.getId());
    }

    @Test
    void testGetSecurityArrangePage() {
        SecurityArrange arrange = new SecurityArrange();
        arrange.setCompanyId(1L);
        arrange.setCommunityId(1L);
        arrange.setArrangeDate(LocalDate.now());
        arrange.setShiftType(1);
        arrange.setStartTime(LocalTime.of(8, 0));
        arrange.setEndTime(LocalTime.of(16, 0));
        arrange.setPosition("大门");
        arrange.setSecurityId(1L);
        arrange.setSecurityName("保安B");
        arrange.setStatus(1);
        arrange.setCreateTime(LocalDateTime.now());
        arrange.setUpdateTime(LocalDateTime.now());
        securityArrangeService.save(arrange);

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<SecurityArrangeVO> result = securityArrangeService.getSecurityArrangePage(query, 1L, null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testCompleteDuty() {
        SecurityArrange arrange = new SecurityArrange();
        arrange.setCompanyId(1L);
        arrange.setCommunityId(1L);
        arrange.setArrangeDate(LocalDate.now());
        arrange.setShiftType(1);
        arrange.setStartTime(LocalTime.of(8, 0));
        arrange.setEndTime(LocalTime.of(16, 0));
        arrange.setPosition("大门");
        arrange.setSecurityId(1L);
        arrange.setSecurityName("保安C");
        arrange.setStatus(1);
        arrange.setCreateTime(LocalDateTime.now());
        arrange.setUpdateTime(LocalDateTime.now());
        securityArrangeService.save(arrange);

        securityArrangeService.completeDuty(arrange.getId(), "test");

        SecurityArrange completed = securityArrangeService.getById(arrange.getId());
        assertEquals(3, completed.getStatus());
    }
}
