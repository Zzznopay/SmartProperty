package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.CommitteeMeetingConverter;
import com.smart.property.operation.domain.CommitteeMeeting;
import com.smart.property.operation.dto.CommitteeMeetingDTO;
import com.smart.property.operation.mapper.CommitteeMeetingMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.CommitteeMeetingService;
import com.smart.property.operation.vo.CommitteeMeetingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommitteeMeetingServiceImpl extends ServiceImpl<CommitteeMeetingMapper, CommitteeMeeting> implements CommitteeMeetingService {

    private final CommitteeMeetingConverter committeeMeetingConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<CommitteeMeetingVO> getPage(PageQuery query, Long companyId, Long communityId) {
        Page<CommitteeMeeting> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CommitteeMeeting> wrapper = new LambdaQueryWrapper<CommitteeMeeting>()
                .eq(CommitteeMeeting::getCompanyId, companyId)
                .eq(CommitteeMeeting::getIsDeleted, 0)
                .eq(communityId != null, CommitteeMeeting::getCommunityId, communityId)
                .orderByDesc(CommitteeMeeting::getMeetingDate);
        Page<CommitteeMeeting> result = baseMapper.selectPage(page, wrapper);
        List<CommitteeMeetingVO> records = result.getRecords().stream()
                .map(committeeMeetingConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, CommitteeMeetingVO::getCommunityId, CommitteeMeetingVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public CommitteeMeetingVO getById(Long id) {
        CommitteeMeeting entity = baseMapper.selectById(id);
        if (entity == null) throw new BusinessException("会议不存在");
        CommitteeMeetingVO vo = committeeMeetingConverter.toVO(entity);
        remoteNameService.fillCommunityNames(List.of(vo), CommitteeMeetingVO::getCommunityId, CommitteeMeetingVO::setCommunityName);
        return vo;
    }

    @Override
    public void create(CommitteeMeetingDTO dto, Long companyId, String operator) {
        CommitteeMeeting entity = committeeMeetingConverter.toEntity(dto);
        entity.setCompanyId(companyId);
        if (entity.getStatus() == null) entity.setStatus(1);
        entity.setCreateBy(operator);
        save(entity);
    }

    @Override
    public void update(Long id, CommitteeMeetingDTO dto, String operator) {
        CommitteeMeeting existing = baseMapper.selectById(id);
        if (existing == null) throw new BusinessException("会议不存在");
        CommitteeMeeting patch = committeeMeetingConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }
}