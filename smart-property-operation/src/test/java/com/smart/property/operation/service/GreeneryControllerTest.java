package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.Greenery;
import com.smart.property.operation.vo.GreeneryVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

/**
 * 绿化Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class GreeneryControllerTest {

    @Autowired
    private GreeneryService greeneryService;

    @Test
    void testAddGreenery() {
        Greenery greenery = new Greenery();
        greenery.setCompanyId(1L);
        greenery.setCommunityId(1L);
        greenery.setGreeneryName("桂花树");
        greenery.setGreeneryType(1);
        greenery.setLocation("小区花园");
        greenery.setQuantity(10);
        greenery.setPlantDate(LocalDate.now());
        greenery.setStatus(1);
        greenery.setCreateBy("test");

        greenery.setCreateTime(java.time.LocalDateTime.now());
        greenery.setUpdateTime(java.time.LocalDateTime.now());
        greeneryService.save(greenery);

        assertNotNull(greenery.getId());
    }

    @Test
    void testGetGreeneryPage() {
        Greenery greenery = new Greenery();
        greenery.setCompanyId(1L);
        greenery.setCommunityId(1L);
        greenery.setGreeneryName("分页测试");
        greenery.setGreeneryType(1);
        greenery.setStatus(1);
        greenery.setCreateTime(java.time.LocalDateTime.now());
        greenery.setUpdateTime(java.time.LocalDateTime.now());
        greeneryService.save(greenery);

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<GreeneryVO> result = greeneryService.getGreeneryPage(query, 1L, null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testUpdateGreenery() {
        Greenery greenery = new Greenery();
        greenery.setCompanyId(1L);
        greenery.setCommunityId(1L);
        greenery.setGreeneryName("更新前");
        greenery.setGreeneryType(1);
        greenery.setStatus(1);
        greenery.setCreateTime(java.time.LocalDateTime.now());
        greenery.setUpdateTime(java.time.LocalDateTime.now());
        greeneryService.save(greenery);

        greenery.setGreeneryName("更新后");
        greenery.setUpdateBy("test");
        greenery.setUpdateTime(java.time.LocalDateTime.now());
        greeneryService.updateById(greenery);

        Greenery updated = greeneryService.getById(greenery.getId());
        assertEquals("更新后", updated.getGreeneryName());
    }

    @Test
    void testDeleteGreenery() {
        Greenery greenery = new Greenery();
        greenery.setCompanyId(1L);
        greenery.setCommunityId(1L);
        greenery.setGreeneryName("删除测试");
        greenery.setGreeneryType(1);
        greenery.setStatus(1);
        greenery.setCreateTime(java.time.LocalDateTime.now());
        greenery.setUpdateTime(java.time.LocalDateTime.now());
        greeneryService.save(greenery);

        greenery.setUpdateTime(java.time.LocalDateTime.now());
        greeneryService.removeById(greenery);

        Greenery deleted = greeneryService.getById(greenery.getId());
        assertNull(deleted);
    }
}
