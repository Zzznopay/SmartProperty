package com.smart.property.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Building;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.FeeItem;
import com.smart.property.property.domain.Ledger;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.domain.Room;
import com.smart.property.property.domain.Unit;
import com.smart.property.property.dto.CommunityDTO;
import com.smart.property.property.dto.OwnerDTO;
import com.smart.property.property.vo.LedgerVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 台帐服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class LedgerServiceTest {

    @Autowired
    private LedgerService ledgerService;

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

    @Autowired
    private FeeItemService feeItemService;

    @Test
    void testSaveLedger() {
        Room room = createRoom("LG01");
        Owner owner = createOwner("LG01-O");
        FeeItem feeItem = createFeeItem(room.getCommunityId(), "LG01-F");

        Ledger ledger = new Ledger();
        ledger.setCompanyId(1L);
        ledger.setCommunityId(room.getCommunityId());
        ledger.setRoomId(room.getId());
        ledger.setOwnerId(owner.getId());
        ledger.setFeeItemId(feeItem.getId());
        ledger.setLedgerMonth("2026-08");
        ledger.setAmount(new BigDecimal("1000"));
        ledger.setPaidAmount(BigDecimal.ZERO);
        ledger.setStatus(1);
        ledger.setCreateBy("test");
        boolean saved = ledgerService.save(ledger);

        assertTrue(saved);
        assertNotNull(ledger.getId());
        LedgerVO loaded = ledgerService.getLedgerById(ledger.getId(), 1L);
        assertNotNull(loaded);
        assertEquals(0, new BigDecimal("1000").compareTo(loaded.getAmount()));
    }

    @Test
    void testGetLedgerPage() {
        Room room = createRoom("LG02");
        Owner owner = createOwner("LG02-O");
        FeeItem feeItem = createFeeItem(room.getCommunityId(), "LG02-F");
        saveLedger(room, owner, feeItem, "2026-08", "500");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<LedgerVO> result = ledgerService.getLedgerPage(query, 1L, room.getId(), null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testGetLedgersByRoomId() {
        Room room = createRoom("LG03");
        Owner owner = createOwner("LG03-O");
        FeeItem feeItem = createFeeItem(room.getCommunityId(), "LG03-F");
        saveLedger(room, owner, feeItem, "2026-08", "800");

        List<LedgerVO> list = ledgerService.getLedgersByRoomId(room.getId(), 1L);

        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    @Test
    void testGetArrearsPage() {
        Room room = createRoom("LG04");
        Owner owner = createOwner("LG04-O");
        FeeItem feeItem = createFeeItem(room.getCommunityId(), "LG04-F");
        saveLedger(room, owner, feeItem, "2026-08", "1000"); // 状态1：未收

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<LedgerVO> result = ledgerService.getArrearsPage(query, 1L);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    /**
     * 保存一条未收台账
     */
    private void saveLedger(Room room, Owner owner, FeeItem feeItem, String month, String amount) {
        Ledger ledger = new Ledger();
        ledger.setCompanyId(1L);
        ledger.setCommunityId(room.getCommunityId());
        ledger.setRoomId(room.getId());
        ledger.setOwnerId(owner.getId());
        ledger.setFeeItemId(feeItem.getId());
        ledger.setLedgerMonth(month);
        ledger.setAmount(new BigDecimal(amount));
        ledger.setPaidAmount(BigDecimal.ZERO);
        ledger.setStatus(1);
        ledger.setCreateBy("test");
        assertTrue(ledgerService.save(ledger));
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

    /**
     * 通过费项服务保存费项并返回实体（含 id）
     */
    private FeeItem createFeeItem(Long communityId, String code) {
        FeeItem feeItem = new FeeItem();
        feeItem.setCompanyId(1L);
        feeItem.setCommunityId(communityId);
        feeItem.setFeeName("物业费-" + code);
        feeItem.setFeeCode(code);
        feeItem.setFeeType(1);
        feeItem.setChargeMode(1);
        feeItem.setUnitPrice(new BigDecimal("2.50"));
        assertTrue(feeItemService.save(feeItem));
        return feeItem;
    }
}
