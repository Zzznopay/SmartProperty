package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 清洁检查 VO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class CleanCheckVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private Long arrangeId;
    private LocalDate checkDate;
    private String areaName;
    private Integer checkResult;
    private Integer score;
    private String problems;
    private Long checkerId;
    private String checkerName;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}