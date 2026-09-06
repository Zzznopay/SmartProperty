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
 * 单元Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class UnitControllerTest {

    @Autowired
    private UnitService unitService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private BuildingService buildingService;

    @Test
    void testAddUnit() {
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
        unitService.save(unit);

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

    @Test
    void testUpdateUnit() {
        Community community = new Community();
        community.setCompanyId(1L);
        community.setCommunityName("单元更新测试");
        community.setCommunityCode("UUPD001");
        community.setStatus(1);
        community.setCreateTime(LocalDateTime.now());
        community.setUpdateTime(LocalDateTime.now());
        communityService.save(community);

        Building building = new Building();
        building.setCompanyId(1L);
        building.setCommunityId(community.getId());
        building.setBuildingName("单元更新楼宇");
        building.setBuildingCode("UBU001");
        building.setBuildingType(1);
        building.setStatus(1);
        building.setCreateTime(LocalDateTime.now());
        building.setUpdateTime(LocalDateTime.now());
        buildingService.save(building);

        Unit unit = new Unit();
        unit.setCompanyId(1L);
        unit.setBuildingId(building.getId());
        unit.setUnitName("更新前");
        unit.setUnitCode("UU001");
        unit.setStatus(1);
        unit.setCreateTime(LocalDateTime.now());
        unit.setUpdateTime(LocalDateTime.now());
        unitService.save(unit);

        unit.setUnitName("更新后");
        unit.setUpdateBy("test");
        unit.setUpdateTime(java.time.LocalDateTime.now());
        unitService.updateById(unit);

        Unit updated = unitService.getById(unit.getId());
        assertEquals("更新后", updated.getUnitName());
    }

    @Test
    void testDeleteUnit() {
        Community community = new Community();
        community.setCompanyId(1L);
        community.setCommunityName("单元删除测试");
        community.setCommunityCode("UDEL001");
        community.setStatus(1);
        community.setCreateTime(LocalDateTime.now());
        community.setUpdateTime(LocalDateTime.now());
        communityService.save(community);

        Building building = new Building();
        building.setCompanyId(1L);
        building.setCommunityId(community.getId());
        building.setBuildingName("单元删除楼宇");
        building.setBuildingCode("UBD001");
        building.setBuildingType(1);
        building.setStatus(1);
        building.setCreateTime(LocalDateTime.now());
        building.setUpdateTime(LocalDateTime.now());
        buildingService.save(building);

        Unit unit = new Unit();
        unit.setCompanyId(1L);
        unit.setBuildingId(building.getId());
        unit.setUnitName("删除测试");
        unit.setUnitCode("UD001");
        unit.setStatus(1);
        unit.setCreateTime(LocalDateTime.now());
        unit.setUpdateTime(LocalDateTime.now());
        unitService.save(unit);

        unit.setUpdateTime(LocalDateTime.now());
        unitService.removeById(unit);

        Unit deleted = unitService.getById(unit.getId());
        assertNull(deleted);
    }
}
