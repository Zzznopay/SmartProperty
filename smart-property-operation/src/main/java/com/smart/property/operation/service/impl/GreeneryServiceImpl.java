package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.GreeneryConverter;
import com.smart.property.operation.domain.Greenery;
import com.smart.property.operation.dto.GreeneryDTO;
import com.smart.property.operation.mapper.GreeneryMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.GreeneryService;
import com.smart.property.operation.vo.GreeneryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 绿化植被服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class GreeneryServiceImpl extends ServiceImpl<GreeneryMapper, Greenery> implements GreeneryService {

    private final GreeneryConverter greeneryConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<GreeneryVO> getGreeneryPage(PageQuery query, Long companyId, Long communityId, Integer greeneryType) {
        Page<Greenery> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<Greenery> wrapper = new LambdaQueryWrapper<Greenery>()
                .eq(Greenery::getCompanyId, companyId)
                .eq(Greenery::getIsDeleted, 0)
                .eq(communityId != null, Greenery::getCommunityId, communityId)
                .eq(greeneryType != null, Greenery::getGreeneryType, greeneryType)
                .orderByAsc(Greenery::getCreateTime);

        Page<Greenery> result = baseMapper.selectPage(page, wrapper);
        List<GreeneryVO> records = result.getRecords().stream()
                .map(greeneryConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, GreeneryVO::getCommunityId, GreeneryVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createGreenery(GreeneryDTO dto, Long companyId, String operator) {
        Greenery greenery = greeneryConverter.toEntity(dto);
        greenery.setCompanyId(companyId);
        if (greenery.getStatus() == null) {
            greenery.setStatus(1);
        }
        greenery.setCreateBy(operator);
        save(greenery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGreenery(Long id, GreeneryDTO dto, String operator) {
        Greenery existing = getById(id);
        if (existing == null) {
            throw new BusinessException("绿化植被不存在");
        }
        Greenery patch = greeneryConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }
}