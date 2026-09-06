package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.OpinionBoxConverter;
import com.smart.property.operation.convert.OpinionSubmitConverter;
import com.smart.property.operation.domain.OpinionBox;
import com.smart.property.operation.domain.OpinionSubmit;
import com.smart.property.operation.dto.OpinionBoxDTO;
import com.smart.property.operation.dto.OpinionSubmitDTO;
import com.smart.property.operation.mapper.OpinionBoxMapper;
import com.smart.property.operation.mapper.OpinionSubmitMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.OpinionBoxService;
import com.smart.property.operation.vo.OpinionBoxVO;
import com.smart.property.operation.vo.OpinionSubmitVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 意见箱服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class OpinionBoxServiceImpl extends ServiceImpl<OpinionBoxMapper, OpinionBox> implements OpinionBoxService {

    private final OpinionSubmitMapper opinionSubmitMapper;
    private final OpinionBoxConverter opinionBoxConverter;
    private final OpinionSubmitConverter opinionSubmitConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<OpinionBoxVO> getOpinionBoxPage(PageQuery query, Long companyId, Long communityId) {
        Page<OpinionBox> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<OpinionBox> wrapper = new LambdaQueryWrapper<OpinionBox>()
                .eq(OpinionBox::getCompanyId, companyId)
                .eq(OpinionBox::getIsDeleted, 0)
                .eq(communityId != null, OpinionBox::getCommunityId, communityId)
                .orderByDesc(OpinionBox::getCreateTime);
        Page<OpinionBox> result = baseMapper.selectPage(page, wrapper);
        List<OpinionBoxVO> records = result.getRecords().stream()
                .map(opinionBoxConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, OpinionBoxVO::getCommunityId, OpinionBoxVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOpinion(OpinionSubmitDTO dto, Long companyId, Long submitUserId, String submitUserName) {
        OpinionSubmit submit = opinionSubmitConverter.toEntity(dto);
        submit.setCompanyId(companyId);
        submit.setSubmitUserId(submitUserId);
        submit.setSubmitUserName(submitUserName);
        submit.setSubmitTime(LocalDateTime.now());
        submit.setStatus(1);
        opinionSubmitMapper.insert(submit);
    }

    @Override
    public PageResult<OpinionSubmitVO> getOpinionSubmitPage(PageQuery query, Long companyId, Long boxId, Integer status) {
        Page<OpinionSubmit> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<OpinionSubmit> wrapper = new LambdaQueryWrapper<OpinionSubmit>()
                .eq(OpinionSubmit::getCompanyId, companyId)
                .eq(OpinionSubmit::getIsDeleted, 0)
                .eq(boxId != null, OpinionSubmit::getBoxId, boxId)
                .eq(status != null, OpinionSubmit::getStatus, status)
                .orderByDesc(OpinionSubmit::getSubmitTime);
        Page<OpinionSubmit> result = opinionSubmitMapper.selectPage(page, wrapper);
        List<OpinionSubmitVO> records = result.getRecords().stream()
                .map(opinionSubmitConverter::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBox(OpinionBoxDTO dto, Long companyId, String operator) {
        OpinionBox box = opinionBoxConverter.toEntity(dto);
        box.setCompanyId(companyId);
        if (box.getIsActive() == null) {
            box.setIsActive(1);
        }
        box.setCreateBy(operator);
        save(box);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeOpinion(Long id, String operator) {
        OpinionSubmit submit = opinionSubmitMapper.selectById(id);
        if (submit == null) {
            throw new BusinessException("意见不存在");
        }
        submit.setStatus(4);
        submit.setUpdateBy(operator);
        opinionSubmitMapper.updateById(submit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyOpinion(Long id, String replyContent, Long replyUserId, String replyUserName) {
        OpinionSubmit submit = opinionSubmitMapper.selectById(id);
        if (submit == null) {
            throw new BusinessException("意见不存在");
        }
        if (submit.getStatus() == 3) {
            throw new BusinessException("已回复");
        }
        submit.setReplyContent(replyContent);
        submit.setReplyUserId(replyUserId);
        submit.setReplyUserName(replyUserName);
        submit.setReplyTime(LocalDateTime.now());
        submit.setStatus(3);
        opinionSubmitMapper.updateById(submit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void evaluateOpinion(Long id, Integer satisfaction) {
        OpinionSubmit submit = opinionSubmitMapper.selectById(id);
        if (submit == null) {
            throw new BusinessException("意见不存在");
        }
        submit.setSatisfaction(satisfaction);
        opinionSubmitMapper.updateById(submit);
    }
}