package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.CleanCheckConverter;
import com.smart.property.operation.domain.CleanCheck;
import com.smart.property.operation.dto.CleanCheckDTO;
import com.smart.property.operation.mapper.CleanCheckMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.CleanCheckService;
import com.smart.property.operation.vo.CleanCheckVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CleanCheckServiceImpl extends ServiceImpl<CleanCheckMapper, CleanCheck> implements CleanCheckService {

    private final CleanCheckConverter cleanCheckConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<CleanCheckVO> getPage(PageQuery query, Long companyId, Long communityId) {
        Page<CleanCheck> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CleanCheck> wrapper = new LambdaQueryWrapper<CleanCheck>()
                .eq(CleanCheck::getCompanyId, companyId)
                .eq(CleanCheck::getIsDeleted, 0)
                .eq(communityId != null, CleanCheck::getCommunityId, communityId)
                .orderByDesc(CleanCheck::getCheckDate);
        Page<CleanCheck> result = baseMapper.selectPage(page, wrapper);
        List<CleanCheckVO> records = result.getRecords().stream()
                .map(cleanCheckConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, CleanCheckVO::getCommunityId, CleanCheckVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public CleanCheckVO getById(Long id) {
        CleanCheck entity = baseMapper.selectById(id);
        if (entity == null) throw new BusinessException("清洁检查不存在");
        CleanCheckVO vo = cleanCheckConverter.toVO(entity);
        remoteNameService.fillCommunityNames(List.of(vo), CleanCheckVO::getCommunityId, CleanCheckVO::setCommunityName);
        return vo;
    }

    @Override
    public void create(CleanCheckDTO dto, Long companyId, String operator) {
        CleanCheck entity = cleanCheckConverter.toEntity(dto);
        entity.setCompanyId(companyId);
        entity.setCreateBy(operator);
        save(entity);
    }

    @Override
    public void update(Long id, CleanCheckDTO dto, String operator) {
        CleanCheck existing = baseMapper.selectById(id);
        if (existing == null) throw new BusinessException("清洁检查不存在");
        CleanCheck patch = cleanCheckConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }
}