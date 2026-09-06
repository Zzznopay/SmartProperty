package com.smart.property.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Building;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.domain.Room;
import com.smart.property.property.domain.Unit;
import com.smart.property.property.dto.CommunityDTO;
import com.smart.property.property.dto.DecorationRecordDTO;
import com.smart.property.property.dto.OwnerDTO;
import com.smart.property.property.vo.DecorationRecordVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 装修记录Controller测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class DecorationRecordControllerTest {

    @Autowired
    private DecorationRecordService decorationRecordService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private OwnerService ownerService;

    @Test
    void testCreateDecoration() {
        Room room = createRoom("DR11");
        Owner owner = createOwner("DR11-O");

        DecorationRecordDTO dto = new DecorationRecordDTO();
        dto.setRoomId(room.getId());
        dto.setOwnerId(owner.getId());
        dto.setDecorationCompany("装修公司A");
        dto.setContactName("张经理");
        dto.setContactPhone("13800138000");
        dto.setDeposit(new BigDecimal("5000"));
        decorationRecordService.createDecoration(dto, 1L, "test");

        DecorationRecordVO created = getFirstByRoom(room.getId());
        assertNotNull(created.getId());
        assertEquals(1, created.getStatus());
    }

    @Test
    void testGetDecorationPage() {
        Room room = createRoom("DR12");
        Owner owner = createOwner("DR12-O");

        DecorationRecordDTO dto = new DecorationRecordDTO();
        dto.setRoomId(room.getId());
        dto.setOwnerId(owner.getId());
        decorationRecordService.createDecoration(dto, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<DecorationRecordVO> result = decorationRecordService.getDecorationPage(query, 1L, room.getId());

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testStartDecoration() {
        Room room = createRoom("DR13");
        Owner owner = createOwner("DR13-O");

        DecorationRecordDTO dto = new DecorationRecordDTO();
        dto.setRoomId(room.getId());
        dto.setOwnerId(owner.getId());
        decorationRecordService.createDecoration(dto, 1L, "test");
        Long id = getFirstByRoom(room.getId()).getId();

        decorationRecordService.startDecoration(id, 1L, "test");

        DecorationRecordVO started = decorationRecordService.getByDecorationId(id, 1L);
        assertEquals(2, started.getStatus());
    }

    @Test
    void testCompleteDecoration() {
        Room room = createRoom("DR14");
        Owner owner = createOwner("DR14-O");

        DecorationRecordDTO dto = new DecorationRecordDTO();
        dto.setRoomId(room.getId());
        dto.setOwnerId(owner.getId());
        decorationRecordService.createDecoration(dto, 1L, "test");
        Long id = getFirstByRoom(room.getId()).getId();

        decorationRecordService.startDecoration(id, 1L, "test");
        decorationRecordService.completeDecoration(id, 1L, "test");

        DecorationRecordVO completed = decorationRecordService.getByDecorationId(id, 1L);
        assertEquals(3, completed.getStatus());
    }

    @Test
    void testCheckDecoration() {
        Room room = createRoom("DR15");
        Owner owner = createOwner("DR15-O");

        DecorationRecordDTO dto = new DecorationRecordDTO();
        dto.setRoomId(room.getId());
        dto.setOwnerId(owner.getId());
        decorationRecordService.createDecoration(dto, 1L, "test");
        Long id = getFirstByRoom(room.getId()).getId();

        decorationRecordService.startDecoration(id, 1L, "test");
        decorationRecordService.completeDecoration(id, 1L, "test");
        decorationRecordService.checkDecoration(id, 1L, 1, "test");

        DecorationRecordVO checked = decorationRecordService.getByDecorationId(id, 1L);
        assertEquals(4, checked.getStatus());
    }

    private DecorationRecordVO getFirstByRoom(Long roomId) {
        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);
        PageResult<DecorationRecordVO> result = decorationRecordService.getDecorationPage(query, 1L, roomId);
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

    /**
     * 通过业主服务创建业主并返回实体（含 id）
     */
    private Owner createOwner(String code) {
        OwnerDTO dto = new OwnerDTO();
        dto.setOwnerCode(code);
        dto.setOwnerName("业主-" + code);
        ownerService.addOwner(dto, 1L, "test");
        return ownerService.getOne(new LambdaQueryWrapper<Owner>()
                .eq(Owner::getOwnerCode, code), false);
    }
}
