package com.smart.property.property.service;

import com.smart.property.property.domain.Building;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.Unit;
import com.smart.property.property.vo.UnitVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 单元服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class UnitServiceTest {

    @Autowired
    private UnitService unitService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private BuildingService buildingService;

    @Test
    void testSaveUnit() {
        Community community = new Community();
        community.setCompanyId(1L);
        community.setCommunityName("单元测试小区");
        community.setCommunityCode("UNIT001");
        community.setStatus(1);
        community.setCreateTime(LocalDateTime.now());
        community.setUpdateTime(LocalDateTime.now());
        communityService.save(community);

        Building building = new Building();
        building.setCompanyId(1L);
        building.setCommunityId(community.getId());
        building.setBuildingName("单元测试楼宇");
        building.setBuildingCode("UB001");
        building.setBuildingType(1);
        building.setStatus(1);
        building.setCreateTime(LocalDateTime.now());
        building.setUpdateTime(LocalDateTime.now());
        buildingService.save(building);

        Unit unit = new Unit();
        unit.setCompanyId(1L);
        unit.setBuildingId(building.getId());
        unit.setUnitName("1单元");
        unit.setUnitCode("U001");
        unit.setFloorCount(6);
        unit.setRoomCount(12);
        unit.setSort(1);
        unit.setStatus(1);
        unit.setCreateBy("test");

        unit.setCreateTime(LocalDateTime.now());
        unit.setUpdateTime(LocalDateTime.now());
        boolean saved = unitService.save(unit);

        assertTrue(saved);
        assertNotNull(unit.getId());
    }

    @Test
    void testGetUnitsByBuildingId() {
        Community community = new Community();
        community.setCompanyId(1L);
        community.setCommunityName("单元列表测试");
        community.setCommunityCode("ULIST001");
        community.setStatus(1);
        community.setCreateTime(LocalDateTime.now());
        community.setUpdateTime(LocalDateTime.now());
        communityService.save(community);

        Building building = new Building();
        building.setCompanyId(1L);
        building.setCommunityId(community.getId());
        building.setBuildingName("单元列表楼宇");
        building.setBuildingCode("UBL001");
        building.setBuildingType(1);
        building.setStatus(1);
        building.setCreateTime(LocalDateTime.now());
        building.setUpdateTime(LocalDateTime.now());
        buildingService.save(building);

        Unit unit = new Unit();
        unit.setCompanyId(1L);
        unit.setBuildingId(building.getId());
        unit.setUnitName("测试单元");
        unit.setUnitCode("UT001");
        unit.setStatus(1);
        unit.setCreateTime(LocalDateTime.now());
        unit.setUpdateTime(LocalDateTime.now());
        unitService.save(unit);

        List<UnitVO> list = unitService.getUnitsByBuildingId(building.getId(), 1L);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }
}
