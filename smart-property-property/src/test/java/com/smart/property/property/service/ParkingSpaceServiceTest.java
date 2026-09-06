package com.smart.property.property.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.domain.Tenant;
import com.smart.property.property.dto.CommunityDTO;
import com.smart.property.property.dto.OwnerDTO;
import com.smart.property.property.dto.ParkingSpaceDTO;
import com.smart.property.property.dto.TenantDTO;
import com.smart.property.property.vo.ParkingSpaceVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 车位服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class ParkingSpaceServiceTest {

    @Autowired
    private ParkingSpaceService parkingSpaceService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private TenantService tenantService;

    @Test
    void testSaveParking() {
        Community community = createCommunity("PK01");

        ParkingSpaceDTO dto = buildParking(community.getId(), "PK-T01");
        parkingSpaceService.createParking(dto, 1L, "test");

        ParkingSpaceVO saved = findByParkingNo(community.getId(), "PK-T01");
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(1, saved.getStatus());
    }

    @Test
    void testGetParkingPage() {
        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<ParkingSpaceVO> result = parkingSpaceService.getParkingPage(query, 1L, null, null);

        assertNotNull(result);
    }

    @Test
    void testSaleParking() {
        Community community = createCommunity("PK03");
        Owner owner = createOwner("PK03-O");

        ParkingSpaceDTO dto = buildParking(community.getId(), "PK-S01");
        parkingSpaceService.createParking(dto, 1L, "test");
        Long id = findByParkingNo(community.getId(), "PK-S01").getId();

        parkingSpaceService.saleParking(id, owner.getId(), new BigDecimal("100000"), 1L, "test");

        ParkingSpaceVO updated = parkingSpaceService.getParkingById(id, 1L);
        assertEquals(2, updated.getStatus());
        assertEquals(owner.getId(), updated.getOwnerId());
    }

    @Test
    void testRentParking() {
        Community community = createCommunity("PK04");
        Tenant tenant = createTenant("PK04-T");

        ParkingSpaceDTO dto = buildParking(community.getId(), "PK-R01");
        parkingSpaceService.createParking(dto, 1L, "test");
        Long id = findByParkingNo(community.getId(), "PK-R01").getId();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);

        parkingSpaceService.rentParking(id, tenant.getId(), new BigDecimal("500"), startDate, endDate, 1L, "test");

        ParkingSpaceVO updated = parkingSpaceService.getParkingById(id, 1L);
        assertEquals(3, updated.getStatus());
        assertEquals(tenant.getId(), updated.getTenantId());
    }

    /**
     * 构建一个车位 DTO
     */
    private ParkingSpaceDTO buildParking(Long communityId, String parkingNo) {
        ParkingSpaceDTO dto = new ParkingSpaceDTO();
        dto.setCommunityId(communityId);
        dto.setParkingNo(parkingNo);
        dto.setParkingType(2);
        dto.setParkingArea(new BigDecimal("12.50"));
        return dto;
    }

    private ParkingSpaceVO findByParkingNo(Long communityId, String parkingNo) {
        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        PageResult<ParkingSpaceVO> result = parkingSpaceService.getParkingPage(query, 1L, communityId, null);
        return result.getRecords().stream()
                .filter(p -> parkingNo.equals(p.getParkingNo()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 通过小区服务创建小区并返回实体（含 id）
     */
    private Community createCommunity(String prefix) {
        CommunityDTO dto = new CommunityDTO();
        dto.setCommunityName("车位测试小区-" + prefix);
        dto.setCommunityCode(prefix + "-C");
        communityService.createCommunity(dto, 1L, "test");
        return communityService.getOne(new LambdaQueryWrapper<Community>()
                .eq(Community::getCommunityCode, prefix + "-C"), false);
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
