package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 消防演练 VO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class FireDrillVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private String drillName;
    private Integer drillType;
    private LocalDate drillDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private Integer participantCount;
    private String drillContent;
    private String drillSummary;
    private String organizer;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}