package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class NoticeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private String noticeTitle;
    private String noticeContent;
    private Integer noticeType;
    private Integer isTop;
    private Integer isPublish;
    private LocalDateTime publishTime;
    private LocalDateTime expireTime;
    private Integer readCount;
    private String images;
    private String attachments;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}