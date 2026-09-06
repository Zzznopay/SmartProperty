package com.smart.property.property.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.dto.CommunityDTO;
import com.smart.property.property.vo.CommunityVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 小区服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class CommunityServiceTest {

    @Autowired
    private CommunityService communityService;

    /**
     * 通过新接口创建小区，并按唯一编码从分页结果中查回创建的 VO
     */
    private CommunityVO createCommunity(String code, String name) {
        CommunityDTO dto = new CommunityDTO();
        dto.setCommunityName(name);
        dto.setCommunityCode(code);
        dto.setAddress("测试地址");
        dto.setArea(new BigDecimal("10000"));
        dto.setPropertyFee(new BigDecimal("2.5"));
        dto.setStatus(1);
        communityService.createCommunity(dto, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        PageResult<CommunityVO> page = communityService.getCommunityPage(query, 1L);
        return page.getRecords().stream()
                .filter(vo -> code.equals(vo.getCommunityCode()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("创建的小区未查询到: " + code));
    }

    @Test
    void testCreateCommunity() {
        CommunityVO created = createCommunity("CSV-T1-01", "测试小区");

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("测试小区", created.getCommunityName());
        assertEquals("CSV-T1-01", created.getCommunityCode());
    }

    @Test
    void testGetCommunityPage() {
        createCommunity("CSV-T1-02", "分页测试小区");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<CommunityVO> result = communityService.getCommunityPage(query, 1L);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testGetCommunityById() {
        CommunityVO created = createCommunity("CSV-T1-03", "详情测试小区");

        CommunityVO result = communityService.getCommunityById(created.getId(), 1L);

        assertNotNull(result);
        assertEquals("详情测试小区", result.getCommunityName());
    }
}
