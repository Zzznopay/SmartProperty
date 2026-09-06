package com.smart.property.operation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 服务工单 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class ServiceOrderDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    private Integer orderType;

    @NotBlank(message = "工单标题不能为空")
    private String title;

    private String content;

    private Long roomId;

    private Long ownerId;

    private String ownerName;

    private String ownerPhone;

    private String images;

    private Integer priority;

    private Integer status;

    private Long assignUserId;

    private String assignUserName;

    private LocalDateTime assignTime;

    private String handleContent;

    private LocalDateTime handleTime;

    private String visitContent;

    private Integer visitScore;

    private LocalDateTime visitTime;

    private LocalDateTime closeTime;

    private String remark;
}