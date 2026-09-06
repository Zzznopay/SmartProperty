package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 抄表 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class MeterReadingVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long roomId;
    private String roomNo;
    private Integer meterType;
    private String meterNo;
    private String readingMonth;
    private BigDecimal lastReading;
    private BigDecimal currentReading;
    private BigDecimal usageAmount;
    private String readingUser;
    private LocalDate readingDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}