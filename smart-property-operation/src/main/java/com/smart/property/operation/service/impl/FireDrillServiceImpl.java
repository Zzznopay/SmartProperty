package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.FireDrillConverter;
import com.smart.property.operation.domain.FireDrill;
import com.smart.property.operation.dto.FireDrillDTO;
import com.smart.property.operation.mapper.FireDrillMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.FireDrillService;
import com.smart.property.operation.vo.FireDrillVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FireDrillServiceImpl extends ServiceImpl<FireDrillMapper, FireDrill> implements FireDrillService {

    private final FireDrillConverter fireDrillConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<FireDrillVO> getPage(PageQuery query, Long companyId, Long communityId) {
        Page<FireDrill> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FireDrill> wrapper = new LambdaQueryWrapper<FireDrill>()
                .eq(FireDrill::getCompanyId, companyId)
                .eq(FireDrill::getIsDeleted, 0)
                .eq(communityId != null, FireDrill::getCommunityId, communityId)
                .orderByDesc(FireDrill::getDrillDate);
        Page<FireDrill> result = baseMapper.selectPage(page, wrapper);
        List<FireDrillVO> records = result.getRecords().stream()
                .map(fireDrillConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, FireDrillVO::getCommunityId, FireDrillVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public FireDrillVO getById(Long id) {
        FireDrill entity = baseMapper.selectById(id);
        if (entity == null) throw new BusinessException("消防演练不存在");
        FireDrillVO vo = fireDrillConverter.toVO(entity);
        remoteNameService.fillCommunityNames(List.of(vo), FireDrillVO::getCommunityId, FireDrillVO::setCommunityName);
        return vo;
    }

    @Override
    public void create(FireDrillDTO dto, Long companyId, String operator) {
        FireDrill entity = fireDrillConverter.toEntity(dto);
        entity.setCompanyId(companyId);
        if (entity.getStatus() == null) entity.setStatus(1);
        entity.setCreateBy(operator);
        save(entity);
    }

    @Override
    public void update(Long id, FireDrillDTO dto, String operator) {
        FireDrill existing = baseMapper.selectById(id);
        if (existing == null) throw new BusinessException("消防演练不存在");
        FireDrill patch = fireDrillConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }
}