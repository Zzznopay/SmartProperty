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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 绿化服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class GreeneryServiceTest {

    @Autowired
    private GreeneryService greeneryService;

    @Test
    void testSaveGreenery() {
        Greenery greenery = new Greenery();
        greenery.setCompanyId(1L);
        greenery.setCommunityId(1L);
        greenery.setGreeneryName("桂花树");
        greenery.setGreeneryType(1); // 乔木
        greenery.setLocation("小区花园");
        greenery.setQuantity(10);
        greenery.setPlantDate(LocalDate.now());
        greenery.setStatus(1);
        greenery.setCreateBy("test");
        greenery.setCreateTime(LocalDateTime.now());
        greenery.setUpdateTime(LocalDateTime.now());

        boolean saved = greeneryService.save(greenery);

        assertTrue(saved);
        assertNotNull(greenery.getId());
    }

    @Test
    void testGetGreeneryPage() {
        Greenery greenery = new Greenery();
        greenery.setCompanyId(1L);
        greenery.setCommunityId(1L);
        greenery.setGreeneryName("草坪");
        greenery.setGreeneryType(3); // 草坪
        greenery.setLocation("中心广场");
        greenery.setQuantity(100);
        greenery.setStatus(1);
        greenery.setCreateTime(LocalDateTime.now());
        greenery.setUpdateTime(LocalDateTime.now());
        greeneryService.save(greenery);

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<GreeneryVO> result = greeneryService.getGreeneryPage(query, 1L, null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }
}
