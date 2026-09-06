package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 消防巡查 VO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class FirePatrolVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private LocalDate patrolDate;
    private LocalTime patrolTime;
    private String patrolArea;
    private Integer patrolResult;
    private String problems;
    private Long patrolUserId;
    private String patrolUserName;
    private String handleContent;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}