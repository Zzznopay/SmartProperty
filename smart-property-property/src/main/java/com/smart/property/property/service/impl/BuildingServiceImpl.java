package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.BuildingConverter;
import com.smart.property.property.domain.Building;
import com.smart.property.property.domain.Community;
import com.smart.property.property.dto.BuildingDTO;
import com.smart.property.property.mapper.BuildingMapper;
import com.smart.property.property.mapper.CommunityMapper;
import com.smart.property.property.service.BuildingService;
import com.smart.property.property.vo.BuildingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 楼宇服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {

    private final BuildingConverter buildingConverter;
    private final CommunityMapper communityMapper;

    /** 批量回填 VO 的所属小区名称（列表接口 VO 只带 communityId） */
    private void fillCommunityNames(List<BuildingVO> records) {
        if (records.isEmpty()) {
            return;
        }
        List<Long> communityIds = records.stream()
                .map(BuildingVO::getCommunityId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (communityIds.isEmpty()) {
            return;
        }
        Map<Long, String> nameMap = communityMapper.selectList(
                        new LambdaQueryWrapper<Community>().in(Community::getId, communityIds))
                .stream()
                .collect(Collectors.toMap(Community::getId, Community::getCommunityName, (a, b) -> a));
        records.forEach(vo -> vo.setCommunityName(nameMap.get(vo.getCommunityId())));
    }

    @Override
    public List<BuildingVO> getBuildingsByCommunityId(Long communityId, Long companyId) {
        List<Building> records = baseMapper.selectList(
                new LambdaQueryWrapper<Building>()
                        .eq(Building::getCommunityId, communityId)
                        .eq(Building::getCompanyId, companyId)
                        .eq(Building::getIsDeleted, 0)
                        .orderByAsc(Building::getCreateTime)
        );
        List<BuildingVO> vos = buildingConverter.toVOList(records);
        fillCommunityNames(vos);
        return vos;
    }

    @Override
    public Page<BuildingVO> getBuildingPage(Page<Building> page,
                                            Long companyId,
                                            Long communityId,
                                            String buildingCode,
                                            String buildingName,
                                            Integer status) {
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<Building>()
                .eq(Building::getCompanyId, companyId)
                .eq(Building::getIsDeleted, 0)
                .eq(communityId != null, Building::getCommunityId, communityId)
                .like(buildingCode != null && !buildingCode.isEmpty(), Building::getBuildingCode, buildingCode)
                .like(buildingName != null && !buildingName.isEmpty(), Building::getBuildingName, buildingName)
                .eq(status != null, Building::getStatus, status)
                .orderByAsc(Building::getCreateTime);
        Page<Building> result = baseMapper.selectPage(page, wrapper);
        Page<BuildingVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<BuildingVO> vos = result.getRecords().stream()
                .map(buildingConverter::toVO)
                .collect(Collectors.toList());
        fillCommunityNames(vos);
        voPage.setRecords(vos);
        return voPage;
    }

    @Override
    public BuildingVO getBuildingById(Long id, Long companyId) {
        BuildingVO vo = buildingConverter.toVO(getCompanyBuilding(id, companyId));
        fillCommunityNames(List.of(vo));
        return vo;
    }

    @Override
    public void createBuilding(BuildingDTO dto, Long companyId, String operator) {
        Building building = buildingConverter.toEntity(dto);
        building.setCompanyId(companyId);
        building.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        building.setCreateBy(operator);
        baseMapper.insert(building);
    }

    @Override
    public void updateBuilding(Long id, BuildingDTO dto, Long companyId, String operator) {
        Building existing = getCompanyBuilding(id, companyId);
        Building patch = buildingConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    public void deleteBuilding(Long id, Long companyId) {
        getCompanyBuilding(id, companyId);
        removeById(id);
    }

    private Building getCompanyBuilding(Long id, Long companyId) {
        Building building = getOne(new LambdaQueryWrapper<Building>()
                .eq(Building::getId, id)
                .eq(Building::getCompanyId, companyId));
        if (building == null) {
            throw new BusinessException("楼宇不存在");
        }
        return building;
    }
}