package com.smart.property.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Building;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.Room;
import com.smart.property.property.domain.Unit;
import com.smart.property.property.dto.CheckRecordDTO;
import com.smart.property.property.dto.CommunityDTO;
import com.smart.property.property.vo.CheckRecordVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验房记录服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class CheckRecordServiceTest {

    @Autowired
    private CheckRecordService checkRecordService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private RoomService roomService;

    @Test
    void testCreateCheckRecord() {
        Room room = createRoom("CR01");

        CheckRecordDTO dto = new CheckRecordDTO();
        dto.setRoomId(room.getId());
        dto.setCheckType(1);
        dto.setCheckDate(LocalDate.now());
        dto.setCheckResult(1);
        dto.setProblems("无");
        checkRecordService.createCheckRecord(dto, 1L, "test");

        CheckRecordVO created = getFirstByRoom(room.getId());
        assertNotNull(created.getId());
        assertEquals(1, created.getStatus());
    }

    @Test
    void testGetCheckRecordPage() {
        Room room = createRoom("CR02");

        CheckRecordDTO dto = new CheckRecordDTO();
        dto.setRoomId(room.getId());
        dto.setCheckType(1);
        dto.setCheckDate(LocalDate.now());
        dto.setCheckResult(2);
        dto.setProblems("墙面有裂缝");
        checkRecordService.createCheckRecord(dto, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<CheckRecordVO> result = checkRecordService.getCheckRecordPage(query, 1L, room.getId());

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testCompleteRectification() {
        Room room = createRoom("CR03");

        CheckRecordDTO dto = new CheckRecordDTO();
        dto.setRoomId(room.getId());
        dto.setCheckType(1);
        dto.setCheckDate(LocalDate.now());
        dto.setCheckResult(2);
        dto.setProblems("需要整改");
        checkRecordService.createCheckRecord(dto, 1L, "test");
        Long id = getFirstByRoom(room.getId()).getId();

        checkRecordService.completeRectification(id, 1L, "test");

        CheckRecordVO completed = checkRecordService.getByCheckId(id, 1L);
        assertEquals(2, completed.getStatus());
    }

    private CheckRecordVO getFirstByRoom(Long roomId) {
        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);
        PageResult<CheckRecordVO> result = checkRecordService.getCheckRecordPage(query, 1L, roomId);
        assertFalse(result.getRecords().isEmpty());
        return result.getRecords().get(0);
    }

    /**
     * 创建 小区-楼宇-单元-房间 前置数据链
     */
    private Room createRoom(String prefix) {
        CommunityDTO communityDTO = new CommunityDTO();
        communityDTO.setCommunityName("测试小区-" + prefix);
        communityDTO.setCommunityCode(prefix + "-C");
        communityService.createCommunity(communityDTO, 1L, "test");
        Community community = communityService.getOne(new LambdaQueryWrapper<Community>()
                .eq(Community::getCommunityCode, prefix + "-C"), false);

        Building building = new Building();
        building.setCompanyId(1L);
        building.setCommunityId(community.getId());
        building.setBuildingName(prefix + "号楼");
        building.setBuildingCode(prefix + "-B");
        building.setBuildingType(1);
        buildingService.save(building);

        Unit unit = new Unit();
        unit.setCompanyId(1L);
        unit.setBuildingId(building.getId());
        unit.setUnitName(prefix + "单元");
        unit.setUnitCode(prefix + "-U");
        unitService.save(unit);

        Room room = new Room();
        room.setCompanyId(1L);
        room.setCommunityId(community.getId());
        room.setBuildingId(building.getId());
        room.setUnitId(unit.getId());
        room.setRoomNo("101");
        room.setRoomCode(prefix + "-R");
        roomService.save(room);
        return room;
    }
}
