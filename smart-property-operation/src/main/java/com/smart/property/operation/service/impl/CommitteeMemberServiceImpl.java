package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.CommitteeMemberConverter;
import com.smart.property.operation.domain.CommitteeMember;
import com.smart.property.operation.dto.CommitteeMemberDTO;
import com.smart.property.operation.mapper.CommitteeMemberMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.CommitteeMemberService;
import com.smart.property.operation.vo.CommitteeMemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommitteeMemberServiceImpl extends ServiceImpl<CommitteeMemberMapper, CommitteeMember> implements CommitteeMemberService {

    private final CommitteeMemberConverter committeeMemberConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<CommitteeMemberVO> getPage(PageQuery query, Long companyId, Long communityId) {
        Page<CommitteeMember> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<CommitteeMember> wrapper = new LambdaQueryWrapper<CommitteeMember>()
                .eq(CommitteeMember::getCompanyId, companyId)
                .eq(CommitteeMember::getIsDeleted, 0)
                .eq(communityId != null, CommitteeMember::getCommunityId, communityId)
                .orderByDesc(CommitteeMember::getCreateTime);
        Page<CommitteeMember> result = baseMapper.selectPage(page, wrapper);
        List<CommitteeMemberVO> records = result.getRecords().stream()
                .map(committeeMemberConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, CommitteeMemberVO::getCommunityId, CommitteeMemberVO::setCommunityName);
        remoteNameService.fillRoomNos(records, CommitteeMemberVO::getRoomId, CommitteeMemberVO::setRoomNo);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public CommitteeMemberVO getById(Long id) {
        CommitteeMember entity = baseMapper.selectById(id);
        if (entity == null) throw new BusinessException("业委会成员不存在");
        CommitteeMemberVO vo = committeeMemberConverter.toVO(entity);
        remoteNameService.fillCommunityNames(List.of(vo), CommitteeMemberVO::getCommunityId, CommitteeMemberVO::setCommunityName);
        remoteNameService.fillRoomNos(List.of(vo), CommitteeMemberVO::getRoomId, CommitteeMemberVO::setRoomNo);
        return vo;
    }

    @Override
    public void create(CommitteeMemberDTO dto, Long companyId, String operator) {
        CommitteeMember entity = committeeMemberConverter.toEntity(dto);
        entity.setCompanyId(companyId);
        if (entity.getStatus() == null) entity.setStatus(1);
        entity.setCreateBy(operator);
        save(entity);
    }

    @Override
    public void update(Long id, CommitteeMemberDTO dto, String operator) {
        CommitteeMember existing = baseMapper.selectById(id);
        if (existing == null) throw new BusinessException("业委会成员不存在");
        CommitteeMember patch = committeeMemberConverter.toEntity(dto);
        patch.setId(id);
        patch.setCompanyId(existing.getCompanyId());
        patch.setCreateBy(existing.getCreateBy());
        patch.setCreateTime(existing.getCreateTime());
        patch.setUpdateBy(operator);
        baseMapper.updateById(patch);
    }
}