package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 社区活动 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class CommunityActivityVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private String activityName;
    private Integer activityType;
    private LocalDate activityDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String content;
    private Integer participantCount;
    private BigDecimal budget;
    private BigDecimal actualCost;
    private String organizer;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}