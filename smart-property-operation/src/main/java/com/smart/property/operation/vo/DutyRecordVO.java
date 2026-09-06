package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 保安执勤 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class DutyRecordVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dutyDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private String position;
    private Long securityId;
    private String securityName;
    private String dutyContent;
    private String abnormalInfo;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}