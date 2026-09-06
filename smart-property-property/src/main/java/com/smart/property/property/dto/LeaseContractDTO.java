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
 * 租赁合同 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class LeaseContractDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "合同编号不能为空")
    private String contractNo;

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    /** 租赁类型：1 整租 2 合租 */
    private Integer leaseType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "起租日期不能为空")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "到期日期不能为空")
    private LocalDate endDate;

    @NotNull(message = "租金不能为空")
    private BigDecimal rentAmount;

    private BigDecimal deposit;

    /** 付款周期：1 月付 2 季付 3 半年付 4 年付 */
    private Integer payCycle;

    /** 1草稿 2生效 3已作废 4已退租 */
    private Integer status;

    private String remark;
}