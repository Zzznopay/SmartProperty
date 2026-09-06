package com.smart.property.property.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Community;
import com.smart.property.property.dto.FeeItemDTO;
import com.smart.property.property.vo.FeeItemVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 费项Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class FeeItemControllerTest {

    @Autowired
    private FeeItemService feeItemService;

    @Autowired
    private CommunityService communityService;

    /**
     * 通过 IService 方式创建前置小区数据（拿到自增主键）
     */
    private Community createCommunity(String code) {
        Community community = new Community();
        community.setCompanyId(1L);
        community.setCommunityName("费项测试小区-" + code);
        community.setCommunityCode(code);
        community.setStatus(1);
        community.setCreateTime(LocalDateTime.now());
        community.setUpdateTime(LocalDateTime.now());
        communityService.save(community);
        return community;
    }

    /**
     * 通过新接口创建费项（非阶梯收费，阶梯配置传 null），并按唯一编码查回创建的 VO
     */
    private FeeItemVO createFeeItem(Long communityId, String feeName, String feeCode) {
        FeeItemDTO dto = new FeeItemDTO();
        dto.setCommunityId(communityId);
        dto.setFeeName(feeName);
        dto.setFeeCode(feeCode);
        dto.setFeeType(1);
        dto.setChargeMode(1);
        dto.setUnitPrice(new BigDecimal("2.5"));
        dto.setUnit("㎡/月");
        dto.setBillingCycle(1);
        dto.setIsLadder(0);
        dto.setIsActive(1);
        feeItemService.createFeeItem(dto, null, 1L, "test");

        return feeItemService.getFeeItemsByCommunityId(communityId, 1L).stream()
                .filter(vo -> feeCode.equals(vo.getFeeCode()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("创建的费项未查询到: " + feeCode));
    }

    @Test
    void testAddFeeItem() {
        Community community = createCommunity("FVC-T1-C01");

        FeeItemVO created = createFeeItem(community.getId(), "物业费", "FVC-T1-F01");

        assertNotNull(created.getId());
        assertEquals("物业费", created.getFeeName());
    }

    @Test
    void testGetFeeItemPage() {
        Community community = createCommunity("FVC-T1-C02");
        createFeeItem(community.getId(), "物业费", "FVC-T1-F02");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<FeeItemVO> result = feeItemService.getFeeItemPage(query, 1L, community.getId());

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testGetFeeItemsByCommunityId() {
        Community community = createCommunity("FVC-T1-C03");
        createFeeItem(community.getId(), "测试费项", "FVC-T1-F03");

        List<FeeItemVO> list = feeItemService.getFeeItemsByCommunityId(community.getId(), 1L);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }
}
