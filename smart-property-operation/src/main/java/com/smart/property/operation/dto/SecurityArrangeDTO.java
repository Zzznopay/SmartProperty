package com.smart.property.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 保安安排 DTO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class SecurityArrangeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrangeDate;

    /** 1 早班 2 中班 3 晚班 */
    private Integer shiftType;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private String position;

    private Long securityId;

    private String securityName;

    private Integer status;

    private String remark;
}