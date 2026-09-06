package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.ParkingSpaceConverter;
import com.smart.property.property.domain.Community;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.domain.ParkingSpace;
import com.smart.property.property.domain.Tenant;
import com.smart.property.property.dto.ParkingSpaceDTO;
import com.smart.property.property.mapper.CommunityMapper;
import com.smart.property.property.mapper.OwnerMapper;
import com.smart.property.property.mapper.ParkingSpaceMapper;
import com.smart.property.property.mapper.TenantMapper;
import com.smart.property.property.service.ParkingSpaceService;
import com.smart.property.property.vo.ParkingSpaceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 车位服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class ParkingSpaceServiceImpl extends ServiceImpl<ParkingSpaceMapper, ParkingSpace> implements ParkingSpaceService {

    private final ParkingSpaceConverter parkingSpaceConverter;
    private final CommunityMapper communityMapper;
    private final OwnerMapper ownerMapper;
    private final TenantMapper tenantMapper;

    /** 批量回填 VO 的小区/业主/租户名称（列表接口 VO 只带 id） */
    private void fillParkingSpaceNames(List<ParkingSpaceVO> records) {
        if (records.isEmpty()) {
            return;
        }
        Map<Long, String> communityNames = communityNamesById(
                records.stream().map(ParkingSpaceVO::getCommunityId).filter(Objects::nonNull).distinct().toList());
        Map<Long, String> ownerNames = ownerNamesById(
                records.stream().map(ParkingSpaceVO::getOwnerId).filter(Objects::nonNull).distinct().toList());
        Map<Long, String> tenantNames = tenantNamesById(
                records.stream().map(ParkingSpaceVO::getTenantId).filter(Objects::nonNull).distinct().toList());
        records.forEach(vo -> {
            vo.setCommunityName(communityNames.get(vo.getCommunityId()));
            vo.setOwnerName(ownerNames.get(vo.getOwnerId()));
            vo.setTenantName(tenantNames.get(vo.getTenantId()));
        });
    }

    private Map<Long, String> communityNamesById(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashMap<>();
        }
        return communityMapper.selectList(new LambdaQueryWrapper<Community>().in(Community::getId, ids))
                .stream()
                .collect(Collectors.toMap(Community::getId, Community::getCommunityName, (a, b) -> a));
    }

    private Map<Long, String> ownerNamesById(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashMap<>();
        }
        return ownerMapper.selectList(new LambdaQueryWrapper<Owner>().in(Owner::getId, ids))
                .stream()
                .collect(Collectors.toMap(Owner::getId, Owner::getOwnerName, (a, b) -> a));
    }

    private Map<Long, String> tenantNamesById(List<Long> ids) {
        if (ids.isEmpty()) {
            return new HashMap<>();
        }
        return tenantMapper.selectList(new LambdaQueryWrapper<Tenant>().in(Tenant::getId, ids))
                .stream()
                .collect(Collectors.toMap(Tenant::getId, Tenant::getTenantName, (a, b) -> a));
    }

    @Override
    public PageResult<ParkingSpaceVO> getParkingPage(PageQuery query, Long companyId, Long communityId, Integer status) {
        Page<ParkingSpace> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<ParkingSpace> wrapper = new LambdaQueryWrapper<ParkingSpace>()
                .eq(ParkingSpace::getCompanyId, companyId)
                .eq(ParkingSpace::getIsDeleted, 0)
                .eq(communityId != null, ParkingSpace::getCommunityId, communityId)
                .eq(status != null, ParkingSpace::getStatus, status)
                .orderByAsc(ParkingSpace::getParkingNo);

        Page<ParkingSpace> result = baseMapper.selectPage(page, wrapper);
        List<ParkingSpaceVO> records = result.getRecords().stream()
                .map(parkingSpaceConverter::toVO)
                .collect(Collectors.toList());
        fillParkingSpaceNames(records);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saleParking(Long id, Long ownerId, BigDecimal salePrice, Long companyId, String operator) {
        ParkingSpace parking = getCompanyParking(id, companyId);
        if (parking.getStatus() != 1) {
            throw new BusinessException("车位不是空闲状态");
        }
        parking.setOwnerId(ownerId);
        parking.setSalePrice(salePrice);
        parking.setSaleDate(LocalDate.now());
        parking.setStatus(2);
        parking.setUpdateBy(operator);
        baseMapper.updateById(parking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rentParking(Long id, Long tenantId, BigDecimal rentPrice, LocalDate startDate, LocalDate endDate,
                            Long companyId, String operator) {
        ParkingSpace parking = getCompanyParking(id, companyId);
        if (parking.getStatus() != 1) {
            throw new BusinessException("车位不是空闲状态");
        }
        parking.setTenantId(tenantId);
        parking.setRentPrice(rentPrice);
        parking.setRentStartDate(startDate);
        parking.setRentEndDate(endDate);
        parking.setStatus(3);
        parking.setUpdateBy(operator);
        baseMapper.updateById(parking);
    }

    @Override
    public ParkingSpaceVO getParkingById(Long id, Long companyId) {
        ParkingSpaceVO vo = parkingSpaceConverter.toVO(getCompanyParking(id, companyId));
        fillParkingSpaceNames(List.of(vo));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createParking(ParkingSpaceDTO dto, Long companyId, String operator) {
        ParkingSpace entity = parkingSpaceConverter.toEntity(dto);
        entity.setCompanyId(companyId);
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        entity.setCreateBy(operator);
        save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateParking(Long id, ParkingSpaceDTO dto, Long companyId, String operator) {
        ParkingSpace existing = getCompanyParking(id, companyId);
        ParkingSpace patch = parkingSpaceConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    public void deleteParking(Long id, Long companyId) {
        getCompanyParking(id, companyId);
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseParking(Long id, Long companyId, String operator) {
        ParkingSpace parking = getCompanyParking(id, companyId);
        if (parking.getStatus() == 1) {
            return;
        }
        parking.setStatus(1);
        parking.setOwnerId(null);
        parking.setTenantId(null);
        parking.setSalePrice(null);
        parking.setSaleDate(null);
        parking.setRentPrice(null);
        parking.setRentStartDate(null);
        parking.setRentEndDate(null);
        parking.setUpdateBy(operator);
        baseMapper.updateById(parking);
    }

    private ParkingSpace getCompanyParking(Long id, Long companyId) {
        ParkingSpace parking = getOne(new LambdaQueryWrapper<ParkingSpace>()
                .eq(ParkingSpace::getId, id)
                .eq(ParkingSpace::getCompanyId, companyId));
        if (parking == null) {
            throw new BusinessException("车位不存在");
        }
        return parking;
    }
}