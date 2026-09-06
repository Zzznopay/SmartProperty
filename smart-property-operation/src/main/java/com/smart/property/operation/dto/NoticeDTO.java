package com.smart.property.operation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class NoticeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @NotBlank(message = "公告标题不能为空")
    private String noticeTitle;

    private String noticeContent;

    private Integer noticeType;

    private Integer isTop;

    private Integer isPublish;

    private LocalDateTime publishTime;

    private LocalDateTime expireTime;

    private String images;

    private String attachments;

    private Integer status;

    private String remark;
}