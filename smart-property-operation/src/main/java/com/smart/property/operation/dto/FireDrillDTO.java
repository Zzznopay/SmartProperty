package com.smart.property.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 消防演练 DTO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class FireDrillDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @NotBlank(message = "演练名称不能为空")
    private String drillName;

    private Integer drillType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate drillDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private String location;

    private Integer participantCount;

    private String drillContent;

    private String drillSummary;

    private String organizer;

    private Integer status;

    private String remark;
}