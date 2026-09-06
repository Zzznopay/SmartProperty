package com.smart.property.operation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 清洁安排 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class CleanArrangeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "小区ID不能为空")
    private Long communityId;

    @NotBlank(message = "区域名称不能为空")
    private String areaName;

    private Integer cleanType;

    private LocalDate arrangeDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Long cleanerId;

    private String cleanerName;

    private String remark;
}
