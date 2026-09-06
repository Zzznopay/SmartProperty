package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.CleanArrange;
import com.smart.property.operation.vo.CleanArrangeVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 清洁安排服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class CleanArrangeServiceTest {

    @Autowired
    private CleanArrangeService cleanArrangeService;

    @Test
    void testSaveCleanArrange() {
        CleanArrange clean = new CleanArrange();
        clean.setCompanyId(1L);
        clean.setCommunityId(1L);
        clean.setAreaName("A区");
        clean.setCleanType(1);
        clean.setArrangeDate(LocalDate.now());
        clean.setStatus(1);
        clean.setCreateBy("test");
        clean.setCreateTime(LocalDateTime.now());
        clean.setUpdateTime(LocalDateTime.now());

        boolean saved = cleanArrangeService.save(clean);

        assertTrue(saved);
        assertNotNull(clean.getId());
    }

    @Test
    void testGetCleanArrangePage() {
        CleanArrange clean = new CleanArrange();
        clean.setCompanyId(1L);
        clean.setCommunityId(1L);
        clean.setAreaName("分页测试");
        clean.setCleanType(1);
        clean.setArrangeDate(LocalDate.now());
        clean.setStatus(1);
        clean.setCreateTime(LocalDateTime.now());
        clean.setUpdateTime(LocalDateTime.now());
        cleanArrangeService.save(clean);

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<CleanArrangeVO> result = cleanArrangeService.getCleanArrangePage(query, 1L, null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testCompleteClean() {
        CleanArrange clean = new CleanArrange();
        clean.setCompanyId(1L);
        clean.setCommunityId(1L);
        clean.setAreaName("完成测试");
        clean.setCleanType(1);
        clean.setArrangeDate(LocalDate.now());
        clean.setStatus(1);
        clean.setCreateTime(LocalDateTime.now());
        clean.setUpdateTime(LocalDateTime.now());
        cleanArrangeService.save(clean);

        cleanArrangeService.completeClean(clean.getId(), "test");

        CleanArrange completed = cleanArrangeService.getById(clean.getId());
        assertEquals(3, completed.getStatus());
        assertNotNull(completed.getCompleteTime());
    }
}
