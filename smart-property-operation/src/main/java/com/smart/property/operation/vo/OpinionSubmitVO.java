package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 意见提交 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class OpinionSubmitVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long boxId;
    private String boxName;
    private String title;
    private String content;
    private Integer isAnonymous;
    private Long submitUserId;
    private String submitUserName;
    private LocalDateTime submitTime;
    private String images;
    private Integer status;
    private String replyContent;
    private Long replyUserId;
    private String replyUserName;
    private LocalDateTime replyTime;
    private Integer satisfaction;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}