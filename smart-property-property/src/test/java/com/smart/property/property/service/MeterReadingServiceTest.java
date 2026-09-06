package com.smart.property.property.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Building;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.Room;
import com.smart.property.property.domain.Unit;
import com.smart.property.property.dto.MeterReadingDTO;
import com.smart.property.property.vo.MeterReadingVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 抄表服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class MeterReadingServiceTest {

    @Autowired
    private MeterReadingService meterReadingService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private RoomService roomService;

    /**
     * 通过各服务的 IService 方式创建 小区->楼宇->单元->房间 前置数据（拿到自增主键）
     */
    private Room createRoomData(String prefix) {
        Community community = new Community();
        community.setCompanyId(1L);
        community.setCommunityName("抄表测试小区");
        community.setCommunityCode(prefix + "-C");
        community.setStatus(1);
        community.setCreateTime(LocalDateTime.now());
        community.setUpdateTime(LocalDateTime.now());
        communityService.save(community);

        Building building = new Building();
        building.setCompanyId(1L);
        building.setCommunityId(community.getId());
        building.setBuildingName("抄表测试楼宇");
        building.setBuildingCode(prefix + "-B");
        building.setBuildingType(1);
        building.setStatus(1);
        building.setCreateTime(LocalDateTime.now());
        building.setUpdateTime(LocalDateTime.now());
        buildingService.save(building);

        Unit unit = new Unit();
        unit.setCompanyId(1L);
        unit.setBuildingId(building.getId());
        unit.setUnitName("1单元");
        unit.setUnitCode(prefix + "-U");
        unit.setStatus(1);
        unit.setCreateTime(LocalDateTime.now());
        unit.setUpdateTime(LocalDateTime.now());
        unitService.save(unit);

        Room room = new Room();
        room.setCompanyId(1L);
        room.setCommunityId(community.getId());
        room.setBuildingId(building.getId());
        room.setUnitId(unit.getId());
        room.setRoomCode(prefix + "-R");
        room.setRoomNo("101");
        room.setFloor(1);
        room.setRoomType(1);
        room.setStatus(1);
        room.setCreateTime(LocalDateTime.now());
        room.setUpdateTime(LocalDateTime.now());
        roomService.save(room);

        return room;
    }

    @Test
    void testCreateMeterReading() {
        Room room = createRoomData("MSV-T1");

        MeterReadingDTO dto = new MeterReadingDTO();
        dto.setRoomId(room.getId());
        dto.setMeterType(1); // 水表
        dto.setMeterNo("MSV-T1-M01");
        dto.setReadingMonth("2026-07");
        dto.setLastReading(new BigDecimal("100.00"));
        dto.setCurrentReading(new BigDecimal("150.00"));
        dto.setReadingUser("抄表员");
        meterReadingService.createMeterReading(dto, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);
        PageResult<MeterReadingVO> page = meterReadingService.getMeterReadingPage(query, 1L, room.getId(), 1, "2026-07");

        MeterReadingVO created = page.getRecords().stream().findFirst().orElse(null);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(room.getId(), created.getRoomId());
        assertNotNull(created.getUsageAmount());
        assertEquals(0, created.getUsageAmount().compareTo(new BigDecimal("50")));
    }

    @Test
    void testGetMeterReadingPage() {
        Room room = createRoomData("MSV-T1P");

        MeterReadingDTO dto = new MeterReadingDTO();
        dto.setRoomId(room.getId());
        dto.setMeterType(2); // 电表
        dto.setMeterNo("MSV-T1P-M01");
        dto.setReadingMonth("2026-07");
        dto.setLastReading(new BigDecimal("1000"));
        dto.setCurrentReading(new BigDecimal("1500"));
        meterReadingService.createMeterReading(dto, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<MeterReadingVO> result = meterReadingService.getMeterReadingPage(query, 1L, room.getId(), null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }
}
