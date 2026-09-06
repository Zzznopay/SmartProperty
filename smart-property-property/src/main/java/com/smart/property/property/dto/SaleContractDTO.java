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
 * 销售合同 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class SaleContractDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "合同编号不能为空")
    private String contractNo;

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    @NotNull(message = "业主ID不能为空")
    private Long ownerId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "合同日期不能为空")
    private LocalDate contractDate;

    @NotNull(message = "销售价格不能为空")
    private BigDecimal salePrice;

    /** 1全款 2按揭 3分期 */
    private Integer payType;

    private BigDecimal downPayment;

    private BigDecimal loanAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    /** 0未交付 1已交付 */
    private Integer deliveryStatus;

    /** 1草稿 2生效 3已作废 */
    private Integer status;

    private String remark;
}