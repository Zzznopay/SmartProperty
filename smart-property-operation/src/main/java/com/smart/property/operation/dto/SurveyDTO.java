package com.smart.property.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投票调查 DTO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class SurveyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @NotBlank(message = "标题不能为空")
    private String surveyTitle;

    private String surveyDesc;

    /** 1 单选 2 多选 */
    private Integer surveyType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Integer isAnonymous;

    private Integer isMultiple;

    private Integer maxSelect;

    private Integer status;

    private String remark;
}