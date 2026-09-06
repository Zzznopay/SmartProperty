package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.property.convert.UnitConverter;
import com.smart.property.property.domain.Building;
import com.smart.property.property.domain.Unit;
import com.smart.property.property.dto.UnitDTO;
import com.smart.property.property.mapper.BuildingMapper;
import com.smart.property.property.mapper.UnitMapper;
import com.smart.property.property.service.UnitService;
import com.smart.property.property.vo.UnitVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 单元服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class UnitServiceImpl extends ServiceImpl<UnitMapper, Unit> implements UnitService {

    private final UnitConverter unitConverter;
    private final BuildingMapper buildingMapper;

    /** 批量回填 VO 的所属楼宇名称（列表接口 VO 只带 buildingId） */
    private void fillBuildingNames(List<UnitVO> records) {
        if (records.isEmpty()) {
            return;
        }
        List<Long> buildingIds = records.stream()
                .map(UnitVO::getBuildingId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (buildingIds.isEmpty()) {
            return;
        }
        Map<Long, String> nameMap = buildingMapper.selectList(
                        new LambdaQueryWrapper<Building>().in(Building::getId, buildingIds))
                .stream()
                .collect(Collectors.toMap(Building::getId, Building::getBuildingName, (a, b) -> a));
        records.forEach(vo -> vo.setBuildingName(nameMap.get(vo.getBuildingId())));
    }

    @Override
    public List<UnitVO> getUnitsByBuildingId(Long buildingId, Long companyId) {
        List<Unit> records = baseMapper.selectList(
                new LambdaQueryWrapper<Unit>()
                        .eq(Unit::getBuildingId, buildingId)
                        .eq(Unit::getCompanyId, companyId)
                        .eq(Unit::getIsDeleted, 0)
                        .orderByAsc(Unit::getSort)
        );
        List<UnitVO> vos = unitConverter.toVOList(records);
        fillBuildingNames(vos);
        return vos;
    }

    @Override
    public Page<UnitVO> getUnitPage(Page<Unit> page,
                                    Long companyId,
                                    Long buildingId,
                                    String unitCode,
                                    String unitName,
                                    Integer status) {
        LambdaQueryWrapper<Unit> wrapper = new LambdaQueryWrapper<Unit>()
                .eq(Unit::getCompanyId, companyId)
                .eq(Unit::getIsDeleted, 0)
                .eq(buildingId != null, Unit::getBuildingId, buildingId)
                .like(unitCode != null && !unitCode.isEmpty(), Unit::getUnitCode, unitCode)
                .like(unitName != null && !unitName.isEmpty(), Unit::getUnitName, unitName)
                .eq(status != null, Unit::getStatus, status)
                .orderByAsc(Unit::getSort);
        Page<Unit> result = baseMapper.selectPage(page, wrapper);
        Page<UnitVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<UnitVO> vos = result.getRecords().stream().map(unitConverter::toVO).collect(Collectors.toList());
        fillBuildingNames(vos);
        voPage.setRecords(vos);
        return voPage;
    }

    @Override
    public UnitVO getUnitById(Long id, Long companyId) {
        UnitVO vo = unitConverter.toVO(getCompanyUnit(id, companyId));
        fillBuildingNames(List.of(vo));
        return vo;
    }

    @Override
    public void createUnit(UnitDTO dto, Long companyId, String operator) {
        Unit unit = unitConverter.toEntity(dto);
        unit.setCompanyId(companyId);
        unit.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        unit.setCreateBy(operator);
        baseMapper.insert(unit);
    }

    @Override
    public void updateUnit(Long id, UnitDTO dto, Long companyId, String operator) {
        Unit existing = getCompanyUnit(id, companyId);
        Unit patch = unitConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    public void deleteUnit(Long id, Long companyId) {
        getCompanyUnit(id, companyId);
        removeById(id);
    }

    private Unit getCompanyUnit(Long id, Long companyId) {
        Unit unit = getOne(new LambdaQueryWrapper<Unit>()
                .eq(Unit::getId, id)
                .eq(Unit::getCompanyId, companyId));
        if (unit == null) {
            throw new BusinessException("单元不存在");
        }
        return unit;
    }
}