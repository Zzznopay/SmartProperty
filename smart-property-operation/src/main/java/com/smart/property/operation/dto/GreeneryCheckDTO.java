package com.smart.property.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 绿化检查 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class GreeneryCheckDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkDate;

    private String areaName;

    /** 1 优 2 良 3 中 4 差 */
    private Integer checkResult;

    private Integer score;

    private String problems;

    private Long checkerId;

    private String checkerName;

    private Integer status;

    private String remark;
}