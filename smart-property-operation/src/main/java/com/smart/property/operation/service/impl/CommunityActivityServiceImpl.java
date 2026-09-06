package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.CommunityActivityConverter;
import com.smart.property.operation.domain.CommunityActivity;
import com.smart.property.operation.dto.CommunityActivityDTO;
import com.smart.property.operation.mapper.CommunityActivityMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.CommunityActivityService;
import com.smart.property.operation.vo.CommunityActivityVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 社区活动服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class CommunityActivityServiceImpl extends ServiceImpl<CommunityActivityMapper, CommunityActivity> implements CommunityActivityService {

    private final CommunityActivityConverter communityActivityConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<CommunityActivityVO> getActivityPage(PageQuery query, Long companyId, Long communityId, Integer activityType) {
        Page<CommunityActivity> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<CommunityActivity> wrapper = new LambdaQueryWrapper<CommunityActivity>()
                .eq(CommunityActivity::getCompanyId, companyId)
                .eq(CommunityActivity::getIsDeleted, 0)
                .eq(communityId != null, CommunityActivity::getCommunityId, communityId)
                .eq(activityType != null, CommunityActivity::getActivityType, activityType)
                .orderByDesc(CommunityActivity::getActivityDate);

        Page<CommunityActivity> result = baseMapper.selectPage(page, wrapper);
        List<CommunityActivityVO> records = result.getRecords().stream()
                .map(communityActivityConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, CommunityActivityVO::getCommunityId, CommunityActivityVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createActivity(CommunityActivityDTO dto, Long companyId, String operator) {
        CommunityActivity activity = communityActivityConverter.toEntity(dto);
        activity.setCompanyId(companyId);
        activity.setCreateBy(operator);
        if (activity.getStatus() == null) {
            activity.setStatus(1);
        }
        save(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(Long id, CommunityActivityDTO dto, String operator) {
        CommunityActivity existing = getById(id);
        if (existing == null) {
            throw new BusinessException("社区活动不存在");
        }
        CommunityActivity patch = communityActivityConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeActivity(Long id, String operator) {
        CommunityActivity activity = getById(id);
        if (activity == null) {
            throw new BusinessException("社区活动不存在");
        }
        if (activity.getStatus() == 3) {
            throw new BusinessException("已完成");
        }
        activity.setStatus(3);
        activity.setUpdateBy(operator);
        baseMapper.updateById(activity);
    }
}