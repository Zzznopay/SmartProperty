package com.smart.property.property.service;

import com.smart.property.property.domain.Community;
import com.smart.property.property.dto.BuildingDTO;
import com.smart.property.property.vo.BuildingVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 楼宇服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class BuildingServiceTest {

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private CommunityService communityService;

    /**
     * 通过 IService 方式创建前置小区数据（拿到自增主键）
     */
    private Community createCommunity(String code) {
        Community community = new Community();
        community.setCompanyId(1L);
        community.setCommunityName("楼宇测试小区-" + code);
        community.setCommunityCode(code);
        community.setStatus(1);
        community.setCreateTime(LocalDateTime.now());
        community.setUpdateTime(LocalDateTime.now());
        communityService.save(community);
        return community;
    }

    /**
     * 通过新接口创建楼宇，并按唯一编码从楼宇列表中查回创建的 VO
     */
    private BuildingVO createBuilding(Long communityId, String code, String name) {
        BuildingDTO dto = new BuildingDTO();
        dto.setCommunityId(communityId);
        dto.setBuildingName(name);
        dto.setBuildingCode(code);
        dto.setBuildingType(1);
        dto.setFloorCount(18);
        dto.setRoomCount(72);
        dto.setStatus(1);
        buildingService.createBuilding(dto, 1L, "test");

        return buildingService.getBuildingsByCommunityId(communityId, 1L).stream()
                .filter(vo -> code.equals(vo.getBuildingCode()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("创建的楼宇未查询到: " + code));
    }

    @Test
    void testCreateBuilding() {
        Community community = createCommunity("BSV-T1-C01");

        BuildingVO created = createBuilding(community.getId(), "BSV-T1-B01", "1号楼");

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("1号楼", created.getBuildingName());
        assertEquals(community.getId(), created.getCommunityId());
    }

    @Test
    void testGetBuildingsByCommunityId() {
        Community community = createCommunity("BSV-T1-C02");
        createBuilding(community.getId(), "BSV-T1-B02", "测试楼宇");

        List<BuildingVO> list = buildingService.getBuildingsByCommunityId(community.getId(), 1L);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }
}
