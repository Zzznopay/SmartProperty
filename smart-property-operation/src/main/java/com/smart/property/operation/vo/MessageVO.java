package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class MessageVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Integer messageType;
    private String title;
    private String content;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String receiverPhone;
    private String receiverEmail;
    private Integer isRead;
    private LocalDateTime readTime;
    private Integer sendStatus;
    private LocalDateTime sendTime;
    private String failReason;
    private String businessType;
    private Long businessId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}