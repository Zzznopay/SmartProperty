package com.smart.property.property.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 抄表 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class MeterReadingDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    /** 表类型：1 水表 2 电表 3 燃气表 4 暖气表 */
    @NotNull(message = "表类型不能为空")
    private Integer meterType;

    private String meterNo;

    /** yyyy-MM */
    @NotBlank(message = "抄表月份不能为空")
    private String readingMonth;

    private BigDecimal lastReading;

    @NotNull(message = "本次读数不能为空")
    private BigDecimal currentReading;

    private BigDecimal usageAmount;

    private String readingUser;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate readingDate;
}