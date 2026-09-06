package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.GreeneryCheckConverter;
import com.smart.property.operation.domain.GreeneryCheck;
import com.smart.property.operation.dto.GreeneryCheckDTO;
import com.smart.property.operation.mapper.GreeneryCheckMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.GreeneryCheckService;
import com.smart.property.operation.vo.GreeneryCheckVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GreeneryCheckServiceImpl extends ServiceImpl<GreeneryCheckMapper, GreeneryCheck> implements GreeneryCheckService {

    private final GreeneryCheckConverter greeneryCheckConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<GreeneryCheckVO> getPage(PageQuery query, Long companyId, Long communityId) {
        Page<GreeneryCheck> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<GreeneryCheck> wrapper = new LambdaQueryWrapper<GreeneryCheck>()
                .eq(GreeneryCheck::getCompanyId, companyId)
                .eq(GreeneryCheck::getIsDeleted, 0)
                .eq(communityId != null, GreeneryCheck::getCommunityId, communityId)
                .orderByDesc(GreeneryCheck::getCheckDate);
        Page<GreeneryCheck> result = baseMapper.selectPage(page, wrapper);
        List<GreeneryCheckVO> records = result.getRecords().stream()
                .map(greeneryCheckConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, GreeneryCheckVO::getCommunityId, GreeneryCheckVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public GreeneryCheckVO getById(Long id) {
        GreeneryCheck entity = baseMapper.selectById(id);
        if (entity == null) throw new BusinessException("绿化检查不存在");
        GreeneryCheckVO vo = greeneryCheckConverter.toVO(entity);
        remoteNameService.fillCommunityNames(List.of(vo), GreeneryCheckVO::getCommunityId, GreeneryCheckVO::setCommunityName);
        return vo;
    }

    @Override
    public void create(GreeneryCheckDTO dto, Long companyId, String operator) {
        GreeneryCheck entity = greeneryCheckConverter.toEntity(dto);
        entity.setCompanyId(companyId);
        entity.setCreateBy(operator);
        save(entity);
    }

    @Override
    public void update(Long id, GreeneryCheckDTO dto, String operator) {
        GreeneryCheck existing = baseMapper.selectById(id);
        if (existing == null) throw new BusinessException("绿化检查不存在");
        GreeneryCheck patch = greeneryCheckConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }
}