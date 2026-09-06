package com.smart.property.property.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 收费记录 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class PaymentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    @NotNull(message = "业主ID不能为空")
    private Long ownerId;

    private BigDecimal totalAmount;

    private BigDecimal actualAmount;

    private BigDecimal discountAmount;

    private Integer payType;

    private LocalDateTime payTime;

    private String receiptNo;

    private String invoiceNo;

    private Long cashierId;

    private String cashierName;

    private String remark;

    /** 收费明细（收取物业费批量明细时使用，可为空） */
    private List<PaymentDetailDTO> details;
}
