package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.FirePatrolConverter;
import com.smart.property.operation.domain.FirePatrol;
import com.smart.property.operation.dto.FirePatrolDTO;
import com.smart.property.operation.mapper.FirePatrolMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.FirePatrolService;
import com.smart.property.operation.vo.FirePatrolVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FirePatrolServiceImpl extends ServiceImpl<FirePatrolMapper, FirePatrol> implements FirePatrolService {

    private final FirePatrolConverter firePatrolConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<FirePatrolVO> getPage(PageQuery query, Long companyId, Long communityId) {
        Page<FirePatrol> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FirePatrol> wrapper = new LambdaQueryWrapper<FirePatrol>()
                .eq(FirePatrol::getCompanyId, companyId)
                .eq(FirePatrol::getIsDeleted, 0)
                .eq(communityId != null, FirePatrol::getCommunityId, communityId)
                .orderByDesc(FirePatrol::getPatrolDate);
        Page<FirePatrol> result = baseMapper.selectPage(page, wrapper);
        List<FirePatrolVO> records = result.getRecords().stream()
                .map(firePatrolConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, FirePatrolVO::getCommunityId, FirePatrolVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public FirePatrolVO getById(Long id) {
        FirePatrol entity = baseMapper.selectById(id);
        if (entity == null) throw new BusinessException("消防巡查不存在");
        FirePatrolVO vo = firePatrolConverter.toVO(entity);
        remoteNameService.fillCommunityNames(List.of(vo), FirePatrolVO::getCommunityId, FirePatrolVO::setCommunityName);
        return vo;
    }

    @Override
    public void create(FirePatrolDTO dto, Long companyId, String operator) {
        FirePatrol entity = firePatrolConverter.toEntity(dto);
        entity.setCompanyId(companyId);
        if (entity.getStatus() == null) entity.setStatus(1);
        entity.setCreateBy(operator);
        save(entity);
    }

    @Override
    public void update(Long id, FirePatrolDTO dto, String operator) {
        FirePatrol existing = baseMapper.selectById(id);
        if (existing == null) throw new BusinessException("消防巡查不存在");
        FirePatrol patch = firePatrolConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }
}