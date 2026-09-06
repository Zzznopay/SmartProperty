package com.smart.property.operation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 来访登记 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class VisitRecordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @NotBlank(message = "访客姓名不能为空")
    private String visitorName;

    private String visitorPhone;

    private String visitReason;

    private String visitTarget;

    private Long roomId;

    private LocalDateTime visitTime;

    private LocalDateTime leaveTime;

    private Integer visitorCount;

    private String plateNo;

    private Integer status;

    private String remark;
}