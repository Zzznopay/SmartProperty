package com.smart.property.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典类型 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class SysDictTypeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "字典名称不能为空")
    @Size(max = 128, message = "字典名称长度不能超过128个字符")
    private String dictName;

    @NotBlank(message = "字典类型不能为空")
    @Size(max = 128, message = "字典类型长度不能超过128个字符")
    private String dictType;

    private Integer status;

    private String remark;
}