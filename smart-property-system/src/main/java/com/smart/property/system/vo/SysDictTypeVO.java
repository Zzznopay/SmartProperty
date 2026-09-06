package com.smart.property.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典类型 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class SysDictTypeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String dictName;
    private String dictType;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}