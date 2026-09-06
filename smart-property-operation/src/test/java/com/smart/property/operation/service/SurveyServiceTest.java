package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.Survey;
import com.smart.property.operation.domain.SurveyOption;
import com.smart.property.operation.dto.SurveyDTO;
import com.smart.property.operation.mapper.SurveyOptionMapper;
import com.smart.property.operation.vo.SurveyOptionVO;
import com.smart.property.operation.vo.SurveyVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 投票调查服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class SurveyServiceTest {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyOptionMapper surveyOptionMapper;

    /**
     * 通过新接口创建调查，并按唯一标题从分页结果中查回创建的 VO
     */
    private SurveyVO createSurvey(String title) {
        SurveyDTO dto = new SurveyDTO();
        dto.setCommunityId(1L);
        dto.setSurveyTitle(title);
        dto.setSurveyDesc("请选择您支持的停车方案");
        dto.setSurveyType(1); // 投票
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now().plusDays(7));
        dto.setIsAnonymous(0);
        dto.setIsMultiple(0);
        surveyService.createSurvey(dto, 1L, "test");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(100);
        PageResult<SurveyVO> page = surveyService.getSurveyPage(query, 1L, null, null);
        return page.getRecords().stream()
                .filter(vo -> title.equals(vo.getSurveyTitle()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("创建的调查未查询到: " + title));
    }

    /**
     * 新接口 createSurvey 不再接收选项列表，选项通过 Mapper 直接落库
     */
    private void insertOption(Long surveyId, String content, int order) {
        SurveyOption option = new SurveyOption();
        option.setCompanyId(1L);
        option.setSurveyId(surveyId);
        option.setOptionContent(content);
        option.setOptionOrder(order);
        option.setVoteCount(0);
        surveyOptionMapper.insert(option);
    }

    @Test
    void testCreateSurvey() {
        SurveyVO created = createSurvey("OD-SV-T1-01 小区停车方案投票");

        assertNotNull(created.getId());
        assertEquals(1, created.getStatus());

        // 验证选项查询（选项经 Mapper 落库后可查回）
        insertOption(created.getId(), "方案A：增加地下车位", 1);
        insertOption(created.getId(), "方案B：立体停车位", 2);

        List<SurveyOptionVO> savedOptions = surveyService.getOptions(created.getId());
        assertEquals(2, savedOptions.size());
    }

    @Test
    void testGetSurveyPage() {
        createSurvey("OD-SV-T2-01 分页测试");

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<SurveyVO> result = surveyService.getSurveyPage(query, 1L, null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testVote() {
        SurveyVO created = createSurvey("OD-SV-T3-01 投票测试");
        insertOption(created.getId(), "选项A", 1);

        // 手动设置状态为进行中
        Survey survey = surveyService.getById(created.getId());
        survey.setStatus(2);
        surveyService.updateById(survey);

        List<SurveyOptionVO> savedOptions = surveyService.getOptions(created.getId());
        assertFalse(savedOptions.isEmpty());

        surveyService.vote(created.getId(), savedOptions.get(0).getId(), 1L, 1L);

        Survey voted = surveyService.getById(created.getId());
        assertEquals(1, voted.getParticipantCount());
    }
}
