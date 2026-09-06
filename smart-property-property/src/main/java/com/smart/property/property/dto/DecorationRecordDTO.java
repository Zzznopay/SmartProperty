package com.smart.property.property.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 装修记录 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class DecorationRecordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    private Long ownerId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applyDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String decorationCompany;

    private String contactName;

    private String contactPhone;

    private BigDecimal deposit;

    private Integer depositStatus;

    private Integer checkResult;

    private Integer status;

    private String remark;
}