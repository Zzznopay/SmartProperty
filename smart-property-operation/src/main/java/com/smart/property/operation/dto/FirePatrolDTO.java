package com.smart.property.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 消防巡查 DTO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class FirePatrolDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate patrolDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime patrolTime;

    private String patrolArea;

    /** 1 正常 2 异常 */
    private Integer patrolResult;

    private String problems;

    private Long patrolUserId;

    private String patrolUserName;

    private String handleContent;

    private Integer status;

    private String remark;
}