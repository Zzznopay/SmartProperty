package com.smart.property.operation.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 投票调查统计 VO
 *
 * @author zzz
 * @since 2026-07-28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyStatisticsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 调查ID */
    private Long surveyId;

    /** 调查标题 */
    private String surveyTitle;

    /** 参与人数 */
    private Integer participantCount;

    /** 选项ID */
    private Long optionId;

    /** 选项内容 */
    private String optionContent;

    /** 选项得票数 */
    private Integer voteCount;

    /** 占比（百分比，保留 2 位小数） */
    private java.math.BigDecimal percentage;
}
