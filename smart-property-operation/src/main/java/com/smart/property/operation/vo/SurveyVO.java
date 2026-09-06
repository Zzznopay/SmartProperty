package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票调查 VO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class SurveyVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private String surveyTitle;
    private String surveyDesc;
    private Integer surveyType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer isAnonymous;
    private Integer isMultiple;
    private Integer maxSelect;
    private Integer participantCount;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}