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
import com.smart.property.property.dto.PaymentDTO;
import com.smart.property.property.dto.PaymentDetailDTO;
import com.smart.property.property.vo.LedgerVO;
import com.smart.property.property.vo.PaymentDetailVO;
import com.smart.property.property.vo.PaymentVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 收费服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

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
    void testCollectPayment() {
        Room room = createRoom("PM01");
        Owner owner = createOwner("PM01-O");
        FeeItem feeItem = createFeeItem(room.getCommunityId(), "PM01-F");
        Ledger ledger = createLedger(room, owner, feeItem, "2026-08", "1000");

        PaymentDTO dto = new PaymentDTO();
        dto.setRoomId(room.getId());
        dto.setOwnerId(owner.getId());
        dto.setTotalAmount(new BigDecimal("1000"));
        dto.setActualAmount(new BigDecimal("1000"));
        dto.setDiscountAmount(BigDecimal.ZERO);
        dto.setPayType(1);
        dto.setPayTime(LocalDateTime.now());

        PaymentDetailDTO detail = new PaymentDetailDTO();
        detail.setLedgerId(ledger.getId());
        detail.setFeeItemId(feeItem.getId());
        detail.setLedgerMonth("2026-08");
        detail.setAmount(new BigDecimal("1000"));
        detail.setActualAmount(new BigDecimal("1000"));

        paymentService.collectPayment(dto, List.of(detail), 1L, "test");

        PaymentVO collected = getFirstByRoom(room.getId(), owner.getId());
        assertNotNull(collected.getId());
        assertNotNull(collected.getPaymentNo());
        assertEquals(1, collected.getStatus());

        List<PaymentDetailVO> details = paymentService.getPaymentDetails(collected.getId());
        assertEquals(1, details.size());

        LedgerVO paid = ledgerService.getLedgerById(ledger.getId(), 1L);
        assertEquals(0, new BigDecimal("1000").compareTo(paid.getPaidAmount()));
        assertEquals(3, paid.getStatus());
    }

    @Test
    void testGetPaymentPage() {
        Room room = createRoom("PM02");
        Owner owner = createOwner("PM02-O");
        collectWithoutDetails(room, owner, "500");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<PaymentVO> result = paymentService.getPaymentPage(query, 1L, room.getId(), null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testRefundPayment() {
        Room room = createRoom("PM03");
        Owner owner = createOwner("PM03-O");
        collectWithoutDetails(room, owner, "1000");
        Long id = getFirstByRoom(room.getId(), owner.getId()).getId();

        paymentService.refundPayment(id, "退款原因", "test");

        PaymentVO refunded = paymentService.getPaymentById(id);
        assertEquals(2, refunded.getStatus());
    }

    @Test
    void testVoidPayment() {
        Room room = createRoom("PM04");
        Owner owner = createOwner("PM04-O");
        collectWithoutDetails(room, owner, "1000");
        Long id = getFirstByRoom(room.getId(), owner.getId()).getId();

        paymentService.voidPayment(id, "作废原因", "test");

        PaymentVO voided = paymentService.getPaymentById(id);
        assertEquals(3, voided.getStatus());
    }

    /**
     * 收取一笔不带明细的费用，并返回生成的收费记录
     */
    private void collectWithoutDetails(Room room, Owner owner, String amount) {
        PaymentDTO dto = new PaymentDTO();
        dto.setRoomId(room.getId());
        dto.setOwnerId(owner.getId());
        dto.setTotalAmount(new BigDecimal(amount));
        dto.setActualAmount(new BigDecimal(amount));
        dto.setPayType(1);
        dto.setPayTime(LocalDateTime.now());
        paymentService.collectPayment(dto, 1L, "test");
    }

    private PaymentVO getFirstByRoom(Long roomId, Long ownerId) {
        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);
        PageResult<PaymentVO> result = paymentService.getPaymentPage(query, 1L, roomId, ownerId);
        assertFalse(result.getRecords().isEmpty());
        return result.getRecords().get(0);
    }

    /**
     * 保存一条未收台账（金额为应收金额）
     */
    private Ledger createLedger(Room room, Owner owner, FeeItem feeItem, String month, String amount) {
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
        return ledger;
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
