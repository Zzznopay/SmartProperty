package com.smart.property.property.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Building;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.Room;
import com.smart.property.property.vo.RoomVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 房间Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class RoomControllerTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private BuildingService buildingService;

    private Long createTestData() {
        Community community = new Community();
        community.setCompanyId(1L);
        community.setCommunityName("房间测试小区");
        community.setCommunityCode("ROOM001");
        community.setStatus(1);
        community.setCreateTime(LocalDateTime.now());
        community.setUpdateTime(LocalDateTime.now());
        communityService.save(community);

        Building building = new Building();
        building.setCompanyId(1L);
        building.setCommunityId(community.getId());
        building.setBuildingName("房间测试楼宇");
        building.setBuildingCode("RB001");
        building.setBuildingType(1);
        building.setStatus(1);
        building.setCreateTime(LocalDateTime.now());
        building.setUpdateTime(LocalDateTime.now());
        buildingService.save(building);

        return building.getId();
    }

    @Test
    void testAddRoom() {
        Long buildingId = createTestData();

        Room room = new Room();
        room.setCompanyId(1L);
        room.setCommunityId(1L);
        room.setBuildingId(buildingId);
        room.setRoomCode("R00101");
        room.setRoomNo("101");
        room.setFloor(1);
        room.setRoomType(1);
        room.setBuildArea(new BigDecimal("89.5"));
        room.setStatus(1);
        room.setCreateBy("test");

        room.setCreateTime(LocalDateTime.now());
        room.setUpdateTime(LocalDateTime.now());
        roomService.save(room);

        assertNotNull(room.getId());
    }

    @Test
    void testGetRoomPage() {
        Long buildingId = createTestData();

        Room room = new Room();
        room.setCompanyId(1L);
        room.setCommunityId(1L);
        room.setBuildingId(buildingId);
        room.setRoomCode("RP00101");
        room.setRoomNo("101");
        room.setFloor(1);
        room.setRoomType(1);
        room.setStatus(1);
        room.setCreateTime(LocalDateTime.now());
        room.setUpdateTime(LocalDateTime.now());
        roomService.save(room);

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<RoomVO> result = roomService.getRoomPage(query, 1L, null, buildingId);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testGetRoomsByBuildingId() {
        Long buildingId = createTestData();

        Room room = new Room();
        room.setCompanyId(1L);
        room.setCommunityId(1L);
        room.setBuildingId(buildingId);
        room.setRoomCode("RL00101");
        room.setRoomNo("101");
        room.setFloor(1);
        room.setRoomType(1);
        room.setStatus(1);
        room.setCreateTime(LocalDateTime.now());
        room.setUpdateTime(LocalDateTime.now());
        roomService.save(room);

        List<RoomVO> list = roomService.getRoomsByBuildingId(buildingId, 1L);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void testUpdateRoom() {
        Long buildingId = createTestData();

        Room room = new Room();
        room.setCompanyId(1L);
        room.setCommunityId(1L);
        room.setBuildingId(buildingId);
        room.setRoomCode("RU00101");
        room.setRoomNo("101");
        room.setFloor(1);
        room.setRoomType(1);
        room.setStatus(1);
        room.setCreateTime(LocalDateTime.now());
        room.setUpdateTime(LocalDateTime.now());
        roomService.save(room);

        room.setRoomNo("102");
        room.setUpdateBy("test");
        room.setUpdateTime(LocalDateTime.now());
        roomService.updateById(room);

        Room updated = roomService.getById(room.getId());
        assertEquals("102", updated.getRoomNo());
    }

    @Test
    void testDeleteRoom() {
        Long buildingId = createTestData();

        Room room = new Room();
        room.setCompanyId(1L);
        room.setCommunityId(1L);
        room.setBuildingId(buildingId);
        room.setRoomCode("RD00101");
        room.setRoomNo("101");
        room.setFloor(1);
        room.setRoomType(1);
        room.setStatus(1);
        room.setCreateTime(LocalDateTime.now());
        room.setUpdateTime(LocalDateTime.now());
        roomService.save(room);

        room.setUpdateTime(LocalDateTime.now());
        room.setUpdateTime(LocalDateTime.now());
        roomService.removeById(room);

        Room deleted = roomService.getById(room.getId());
        assertNull(deleted);
    }
}
