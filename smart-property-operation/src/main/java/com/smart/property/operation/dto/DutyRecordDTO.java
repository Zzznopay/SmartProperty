package com.smart.property.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 保安执勤 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class DutyRecordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

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
}