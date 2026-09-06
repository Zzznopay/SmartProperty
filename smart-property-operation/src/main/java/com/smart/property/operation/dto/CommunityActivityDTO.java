package com.smart.property.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 社区活动 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class CommunityActivityDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @NotBlank(message = "活动名称不能为空")
    private String activityName;

    private Integer activityType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private String location;

    private String content;

    private Integer participantCount;

    private BigDecimal budget;

    private BigDecimal actualCost;

    private String organizer;

    private Integer status;

    private String remark;
}