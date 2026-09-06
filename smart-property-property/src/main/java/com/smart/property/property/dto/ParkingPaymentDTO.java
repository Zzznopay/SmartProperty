package com.smart.property.property.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车位缴费记录 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class ParkingPaymentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "车位ID不能为空")
    private Long parkingId;

    private String feeMonth;

    /** 1服务费 2租金 */
    private Integer feeType;

    private BigDecimal amount;

    private BigDecimal actualAmount;

    private LocalDateTime payTime;

    /** 1现金 2转账 3微信 4支付宝 */
    private Integer payType;

    private String remark;
}
