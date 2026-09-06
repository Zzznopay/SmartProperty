package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.Regulation;
import com.smart.property.operation.vo.RegulationVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

/**
 * 规章制度Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class RegulationControllerTest {

    @Autowired
    private RegulationService regulationService;

    @Test
    void testAddRegulation() {
        Regulation regulation = new Regulation();
        regulation.setCompanyId(1L);
        regulation.setTitle("物业管理制度");
        regulation.setContent("管理制度内容...");
        regulation.setCategory("管理制度");
        regulation.setStatus(1);
        regulation.setViewCount(0);
        regulation.setCreateBy("test");

        regulation.setCreateTime(java.time.LocalDateTime.now());
        regulation.setUpdateTime(java.time.LocalDateTime.now());
        regulationService.save(regulation);

        assertNotNull(regulation.getId());
    }

    @Test
    void testGetRegulationPage() {
        Regulation regulation = new Regulation();
        regulation.setCompanyId(1L);
        regulation.setTitle("分页测试");
        regulation.setContent("测试内容");
        regulation.setCategory("安全制度");
        regulation.setStatus(1);
        regulation.setViewCount(0);
        regulation.setCreateTime(java.time.LocalDateTime.now());
        regulation.setUpdateTime(java.time.LocalDateTime.now());
        regulationService.save(regulation);

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<RegulationVO> result = regulationService.getRegulationPage(query, 1L, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testPublishRegulation() {
        Regulation regulation = new Regulation();
        regulation.setCompanyId(1L);
        regulation.setTitle("发布测试");
        regulation.setContent("测试内容");
        regulation.setCategory("管理制度");
        regulation.setStatus(1);
        regulation.setViewCount(0);
        regulation.setCreateTime(java.time.LocalDateTime.now());
        regulation.setUpdateTime(java.time.LocalDateTime.now());
        regulationService.save(regulation);

        regulationService.publishRegulation(regulation.getId(), "test");

        Regulation published = regulationService.getById(regulation.getId());
        assertEquals(2, published.getStatus());
    }

    @Test
    void testIncrementViewCount() {
        Regulation regulation = new Regulation();
        regulation.setCompanyId(1L);
        regulation.setTitle("查阅测试");
        regulation.setContent("测试内容");
        regulation.setCategory("管理制度");
        regulation.setStatus(2);
        regulation.setViewCount(0);
        regulation.setCreateTime(java.time.LocalDateTime.now());
        regulation.setUpdateTime(java.time.LocalDateTime.now());
        regulationService.save(regulation);

        regulationService.incrementViewCount(regulation.getId());
        regulationService.incrementViewCount(regulation.getId());

        Regulation viewed = regulationService.getById(regulation.getId());
        assertEquals(2, viewed.getViewCount());
    }
}
