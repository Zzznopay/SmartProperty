package com.smart.property.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Building;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.Room;
import com.smart.property.property.domain.Tenant;
import com.smart.property.property.domain.Unit;
import com.smart.property.property.dto.CommunityDTO;
import com.smart.property.property.dto.LeaseContractDTO;
import com.smart.property.property.dto.TenantDTO;
import com.smart.property.property.vo.LeaseContractVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 租赁合同服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class LeaseContractServiceTest {

    @Autowired
    private LeaseContractService leaseContractService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private TenantService tenantService;

    @Test
    void testSaveContract() {
        Room room = createRoom("LC01");
        Tenant tenant = createTenant("LC01-T");

        LeaseContractDTO dto = buildContract("LC-S1", room.getId(), tenant.getId(), 1);
        leaseContractService.createContract(dto, 1L, "test");

        LeaseContractVO saved = findByContractNo("LC-S1");
        assertNotNull(saved);
        LeaseContractVO loaded = leaseContractService.getContractById(saved.getId(), 1L);
        assertEquals("LC-S1", loaded.getContractNo());
        assertEquals(1, loaded.getStatus());
    }

    @Test
    void testGetContractPage() {
        Room room = createRoom("LC02");
        Tenant tenant = createTenant("LC02-T");

        LeaseContractDTO dto = buildContract("LC-P1", room.getId(), tenant.getId(), 1);
        leaseContractService.createContract(dto, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<LeaseContractVO> result = leaseContractService.getContractPage(query, 1L, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testTerminateContract() {
        Room room = createRoom("LC03");
        Tenant tenant = createTenant("LC03-T");

        // 状态2：生效
        LeaseContractDTO dto = buildContract("LC-T1", room.getId(), tenant.getId(), 2);
        leaseContractService.createContract(dto, 1L, "test");
        Long id = findByContractNo("LC-T1").getId();

        leaseContractService.terminateContract(id, 1L, "租户提前退租", "test");

        LeaseContractVO terminated = leaseContractService.getContractById(id, 1L);
        assertEquals(3, terminated.getStatus());
        assertNotNull(terminated.getTerminateDate());
    }

    @Test
    void testGetExpiringContracts() {
        Room room = createRoom("LC04");
        Tenant tenant = createTenant("LC04-T");

        LeaseContractDTO dto = buildContract("LC-E1", room.getId(), tenant.getId(), 2);
        dto.setStartDate(LocalDate.now().minusMonths(11));
        dto.setEndDate(LocalDate.now().plusDays(15)); // 15天后到期
        dto.setRentAmount(new BigDecimal("2200"));
        leaseContractService.createContract(dto, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<LeaseContractVO> result = leaseContractService.getExpiringContracts(query, 1L, 30);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    /**
     * 构建一个合同 DTO（默认一年期，月租 2000）
     */
    private LeaseContractDTO buildContract(String contractNo, Long roomId, Long tenantId, int status) {
        LeaseContractDTO dto = new LeaseContractDTO();
        dto.setContractNo(contractNo);
        dto.setRoomId(roomId);
        dto.setTenantId(tenantId);
        dto.setLeaseType(1);
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusYears(1));
        dto.setRentAmount(new BigDecimal("2000"));
        dto.setPayCycle(1);
        dto.setStatus(status);
        return dto;
    }

    private LeaseContractVO findByContractNo(String contractNo) {
        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        PageResult<LeaseContractVO> result = leaseContractService.getContractPage(query, 1L, null);
        return result.getRecords().stream()
                .filter(c -> contractNo.equals(c.getContractNo()))
                .findFirst()
                .orElse(null);
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
     * 通过租户服务创建租户并返回实体（含 id）
     */
    private Tenant createTenant(String code) {
        TenantDTO dto = new TenantDTO();
        dto.setTenantCode(code);
        dto.setTenantName("租户-" + code);
        tenantService.createTenant(dto, 1L, "test");
        return tenantService.getOne(new LambdaQueryWrapper<Tenant>()
                .eq(Tenant::getTenantCode, code), false);
    }
}
