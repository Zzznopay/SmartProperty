package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.Survey;
import com.smart.property.operation.dto.SurveyDTO;
import com.smart.property.operation.vo.SurveyOptionVO;
import com.smart.property.operation.vo.SurveyStatisticsVO;
import com.smart.property.operation.vo.SurveyVO;

import java.util.List;

/**
 * 投票调查服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface SurveyService extends IService<Survey> {

    PageResult<SurveyVO> getSurveyPage(PageQuery query, Long companyId, Long communityId, Integer status);

    SurveyVO getByIdVO(Long id);

    void createSurvey(SurveyDTO dto, Long companyId, String operator);

    void vote(Long surveyId, Long optionId, Long userId, Long companyId);

    List<SurveyOptionVO> getOptions(Long surveyId);

    List<SurveyStatisticsVO> getSurveyStatistics(Long surveyId);

    void finishSurvey(Long surveyId, String operator);
}