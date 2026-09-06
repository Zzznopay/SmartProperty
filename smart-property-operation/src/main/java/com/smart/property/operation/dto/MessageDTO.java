package com.smart.property.operation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class MessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "消息类型不能为空")
    private Integer messageType;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    @NotNull(message = "接收人ID不能为空")
    private Long receiverId;

    private String receiverName;

    private String receiverPhone;

    private String receiverEmail;

    private String businessType;

    private Long businessId;
}