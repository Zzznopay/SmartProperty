package com.smart.property.property.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 票据 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class InvoiceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "票据号不能为空")
    private String invoiceNo;

    /** 1 收据 2 发票 */
    private Integer invoiceType;

    private Long userId;

    private String userName;
}