package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 票据 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class InvoiceVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private String invoiceNo;
    private Integer invoiceType;
    private Long userId;
    private String userName;
    private Integer status;
    private LocalDateTime useTime;
    private LocalDateTime voidTime;
    private String voidReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}