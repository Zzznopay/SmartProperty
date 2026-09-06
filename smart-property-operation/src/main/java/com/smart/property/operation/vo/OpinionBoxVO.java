package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 意见箱 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class OpinionBoxVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private String boxName;
    private Long adminUserId;
    private String adminUserName;
    private Integer isAnonymous;
    private Integer isActive;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}