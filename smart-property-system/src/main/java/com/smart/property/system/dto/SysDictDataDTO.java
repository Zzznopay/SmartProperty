package com.smart.property.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典数据 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class SysDictDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "字典类型不能为空")
    @Size(max = 128, message = "字典类型长度不能超过128个字符")
    private String dictType;

    @NotBlank(message = "字典标签不能为空")
    @Size(max = 128, message = "字典标签长度不能超过128个字符")
    private String dictLabel;

    @NotBlank(message = "字典键值不能为空")
    @Size(max = 128, message = "字典键值长度不能超过128个字符")
    private String dictValue;

    private Integer sort;

    private Integer status;

    private String remark;
}