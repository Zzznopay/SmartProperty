package com.smart.property.common.core.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页查询基类
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
public class PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 页码(从1开始) */
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    /** 每页条数 */
    @Min(value = 1, message = "每页条数最小为1")
    @Max(value = 500, message = "每页条数最大为500")
    private Integer pageSize = 10;

    /** 计算偏移量 */
    public long getOffset() {
        return (long) (pageNum - 1) * pageSize;
    }
}
