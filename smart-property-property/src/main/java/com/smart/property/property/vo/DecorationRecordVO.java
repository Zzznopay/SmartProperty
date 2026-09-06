package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 装修记录 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class DecorationRecordVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long roomId;
    private String roomNo;
    private Long ownerId;
    private String ownerName;
    private LocalDate applyDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String decorationCompany;
    private String contactName;
    private String contactPhone;
    private BigDecimal deposit;
    private Integer depositStatus;
    private Integer checkResult;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}