package com.smart.property.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 绿化植被 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class GreeneryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @NotBlank(message = "植被名称不能为空")
    private String greeneryName;

    private Integer greeneryType;

    private String location;

    private Integer quantity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate plantDate;

    private Integer status;

    private String remark;
}