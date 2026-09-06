package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.operation.convert.SurveyConverter;
import com.smart.property.operation.convert.SurveyOptionConverter;
import com.smart.property.operation.domain.Survey;
import com.smart.property.operation.domain.SurveyOption;
import com.smart.property.operation.domain.SurveyVote;
import com.smart.property.operation.dto.SurveyDTO;
import com.smart.property.operation.mapper.SurveyMapper;
import com.smart.property.operation.mapper.SurveyOptionMapper;
import com.smart.property.operation.mapper.SurveyVoteMapper;
import com.smart.property.operation.remote.RemoteNameService;
import com.smart.property.operation.service.SurveyService;
import com.smart.property.operation.vo.SurveyOptionVO;
import com.smart.property.operation.vo.SurveyStatisticsVO;
import com.smart.property.operation.vo.SurveyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 投票调查服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class SurveyServiceImpl extends ServiceImpl<SurveyMapper, Survey> implements SurveyService {

    private final SurveyOptionMapper surveyOptionMapper;
    private final SurveyVoteMapper surveyVoteMapper;
    private final SurveyConverter surveyConverter;
    private final SurveyOptionConverter surveyOptionConverter;
    private final RemoteNameService remoteNameService;

    @Override
    public PageResult<SurveyVO> getSurveyPage(PageQuery query, Long companyId, Long communityId, Integer status) {
        Page<Survey> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Survey> wrapper = new LambdaQueryWrapper<Survey>()
                .eq(Survey::getCompanyId, companyId)
                .eq(Survey::getIsDeleted, 0)
                .eq(communityId != null, Survey::getCommunityId, communityId)
                .eq(status != null, Survey::getStatus, status)
                .orderByDesc(Survey::getCreateTime);
        Page<Survey> result = baseMapper.selectPage(page, wrapper);
        List<SurveyVO> records = result.getRecords().stream()
                .map(surveyConverter::toVO)
                .collect(Collectors.toList());
        remoteNameService.fillCommunityNames(records, SurveyVO::getCommunityId, SurveyVO::setCommunityName);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public SurveyVO getByIdVO(Long id) {
        Survey survey = getById(id);
        if (survey == null) {
            throw new BusinessException("投票调查不存在");
        }
        SurveyVO vo = surveyConverter.toVO(survey);
        remoteNameService.fillCommunityNames(List.of(vo), SurveyVO::getCommunityId, SurveyVO::setCommunityName);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSurvey(SurveyDTO dto, Long companyId, String operator) {
        Survey survey = surveyConverter.toEntity(dto);
        survey.setCompanyId(companyId);
        survey.setParticipantCount(0);
        survey.setStatus(1);
        survey.setCreateBy(operator);
        baseMapper.insert(survey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void vote(Long surveyId, Long optionId, Long userId, Long companyId) {
        Survey survey = getById(surveyId);
        if (survey == null) {
            throw new BusinessException("投票调查不存在");
        }
        if (survey.getStatus() != 2) {
            throw new BusinessException("投票未进行中");
        }
        Long count = surveyVoteMapper.selectCount(
                new LambdaQueryWrapper<SurveyVote>()
                        .eq(SurveyVote::getSurveyId, surveyId)
                        .eq(SurveyVote::getUserId, userId)
        );
        if (count > 0) {
            throw new BusinessException("已投过票");
        }
        SurveyVote vote = new SurveyVote();
        vote.setCompanyId(companyId);
        vote.setSurveyId(surveyId);
        vote.setOptionId(optionId);
        vote.setUserId(userId);
        vote.setVoteTime(LocalDateTime.now());
        surveyVoteMapper.insert(vote);

        SurveyOption option = surveyOptionMapper.selectById(optionId);
        if (option != null) {
            option.setVoteCount(option.getVoteCount() + 1);
            surveyOptionMapper.updateById(option);
        }
        survey.setParticipantCount(survey.getParticipantCount() + 1);
        baseMapper.updateById(survey);
    }

    @Override
    public List<SurveyOptionVO> getOptions(Long surveyId) {
        List<SurveyOption> options = surveyOptionMapper.selectList(
                new LambdaQueryWrapper<SurveyOption>()
                        .eq(SurveyOption::getSurveyId, surveyId)
                        .orderByAsc(SurveyOption::getOptionOrder)
        );
        return surveyOptionConverter.toVOList(options);
    }

    @Override
    public List<SurveyStatisticsVO> getSurveyStatistics(Long surveyId) {
        Survey survey = getById(surveyId);
        if (survey == null) {
            throw new BusinessException("投票调查不存在");
        }
        List<SurveyOptionVO> options = getOptions(surveyId);
        int participantCount = survey.getParticipantCount() == null ? 0 : survey.getParticipantCount();
        List<SurveyStatisticsVO> result = new ArrayList<>(options.size());
        for (SurveyOptionVO option : options) {
            int votes = option.getVoteCount() == null ? 0 : option.getVoteCount();
            BigDecimal percentage = participantCount > 0
                    ? BigDecimal.valueOf(votes * 100.0 / participantCount).setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            result.add(new SurveyStatisticsVO(surveyId, survey.getSurveyTitle(),
                    participantCount, option.getId(), option.getOptionContent(), votes, percentage));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishSurvey(Long surveyId, String operator) {
        Survey survey = getById(surveyId);
        if (survey == null) {
            throw new BusinessException("投票调查不存在");
        }
        if (survey.getStatus() == 3) {
            return;
        }
        survey.setStatus(3);
        survey.setUpdateBy(operator);
        baseMapper.updateById(survey);
    }
}