package com.smart.property.common.core.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应对象
 *
 * @param <T> 数据类型
 * @author zzz
 * @since 2026-07-25
 */
@Data
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 当前页码 */
    private long pageNum;

    /** 每页条数 */
    private long pageSize;

    /** 总页数 */
    private long pages;

    /** 数据列表 */
    private List<T> records;

    public PageResult() {
        this.records = Collections.emptyList();
    }

    public PageResult(long total, long pageNum, long pageSize, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pageSize > 0 ? (total + pageSize - 1) / pageSize : 0;
        this.records = records;
    }
}
