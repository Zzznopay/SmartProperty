package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.convert.BlueprintConverter;
import com.smart.property.property.domain.Blueprint;
import com.smart.property.property.dto.BlueprintDTO;
import com.smart.property.property.mapper.BlueprintMapper;
import com.smart.property.property.service.BlueprintService;
import com.smart.property.property.vo.BlueprintVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图纸服务实现
 *
 * @author zzz
 * @since 2026-07-28
 */
@Service
@RequiredArgsConstructor
public class BlueprintServiceImpl extends ServiceImpl<BlueprintMapper, Blueprint> implements BlueprintService {

    private final BlueprintConverter blueprintConverter;

    @Override
    public PageResult<BlueprintVO> getBlueprintPage(PageQuery query, Long companyId, Long communityId, Long buildingId, Integer blueprintType) {
        Page<Blueprint> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Blueprint> wrapper = new LambdaQueryWrapper<Blueprint>()
                .eq(Blueprint::getCompanyId, companyId)
                .eq(Blueprint::getIsDeleted, 0)
                .eq(communityId != null, Blueprint::getCommunityId, communityId)
                .eq(buildingId != null, Blueprint::getBuildingId, buildingId)
                .eq(blueprintType != null, Blueprint::getBlueprintType, blueprintType)
                .orderByDesc(Blueprint::getCreateTime);
        Page<Blueprint> result = baseMapper.selectPage(page, wrapper);
        List<BlueprintVO> records = result.getRecords().stream()
                .map(blueprintConverter::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public void createBlueprint(BlueprintDTO dto, Long companyId, String operator) {
        Blueprint blueprint = blueprintConverter.toEntity(dto);
        blueprint.setCompanyId(companyId);
        blueprint.setCreateBy(operator);
        blueprint.setUploadUser(operator);
        save(blueprint);
    }
}