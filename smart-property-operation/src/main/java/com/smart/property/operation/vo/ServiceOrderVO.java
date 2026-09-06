package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 服务工单 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class ServiceOrderVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private String orderNo;
    private Integer orderType;
    private String title;
    private String content;
    private Long roomId;
    private String roomNo;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}