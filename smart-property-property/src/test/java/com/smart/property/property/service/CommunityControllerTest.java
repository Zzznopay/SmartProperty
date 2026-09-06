package com.smart.property.property.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.dto.CommunityDTO;
import com.smart.property.property.vo.CommunityVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 小区Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class CommunityControllerTest {

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
    void testAddCommunity() {
        CommunityVO created = createCommunity("CVC-T1-01", "测试小区A");

        assertNotNull(created.getId());
        assertEquals("测试小区A", created.getCommunityName());
        assertEquals("CVC-T1-01", created.getCommunityCode());
    }

    @Test
    void testUpdateCommunity() {
        CommunityVO created = createCommunity("CVC-T1-02", "更新前");

        CommunityDTO update = new CommunityDTO();
        update.setCommunityName("更新后");
        update.setCommunityCode("CVC-T1-02");
        update.setAddress("更新后的地址");
        communityService.updateCommunity(created.getId(), update, 1L, "test");

        CommunityVO updated = communityService.getCommunityById(created.getId(), 1L);
        assertEquals("更新后", updated.getCommunityName());
        assertEquals("更新后的地址", updated.getAddress());
    }

    @Test
    void testDeleteCommunity() {
        CommunityVO created = createCommunity("CVC-T1-03", "删除测试");

        communityService.deleteCommunity(created.getId(), 1L);

        // 逻辑删除后按公司隔离查询会抛业务异常
        assertThrows(BusinessException.class,
                () -> communityService.getCommunityById(created.getId(), 1L));
    }
}
