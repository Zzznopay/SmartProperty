package com.smart.property.property.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 验房记录 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class CheckRecordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    private Long ownerId;

    /** 1交付前 2交付时 3交付后 */
    private Integer checkType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkDate;

    /** 1 通过 2 有问题 3 不通过 */
    private Integer checkResult;

    private String problems;

    private Integer status;

    private String remark;
}