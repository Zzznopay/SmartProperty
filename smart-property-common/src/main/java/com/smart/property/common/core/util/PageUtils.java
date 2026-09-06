package com.smart.property.common.core.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页工具类
 * <p>统一 MyBatis-Plus IPage → PageResult 转换；不提供 buildPage，复杂查询继续使用 Page.of()。</p>
 *
 * @author zzz
 * @since 2026-07-31
 */
public final class PageUtils {

    private PageUtils() {
    }

    /**
     * IPage → PageResult（实体同型）
     */
    public static <T> PageResult<T> toPage(IPage<T> page) {
        if (page == null) {
            return new PageResult<>(0L, 1L, 10L, Collections.emptyList());
        }
        return new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), page.getRecords());
    }

    /**
     * IPage → PageResult（实体转换，records 通过 converter 映射到 VO）
     */
    public static <E, V> PageResult<V> toPage(IPage<E> page, Function<E, V> converter) {
        if (page == null) {
            return new PageResult<>(0L, 1L, 10L, Collections.emptyList());
        }
        List<V> records = page.getRecords() == null ? Collections.emptyList()
                : page.getRecords().stream().map(converter).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

    /**
     * 默认 PageQuery（pageNum=1, pageSize=10）
     */
    public static PageQuery defaultPage() {
        return new PageQuery();
    }

    /**
     * 安全获取 pageNum，未传则 1
     */
    public static long safePageNum(Long pageNum) {
        return pageNum == null || pageNum < 1 ? 1L : pageNum;
    }

    /**
     * 安全获取 pageSize，未传则 10；上限 200
     */
    public static long safePageSize(Long pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10L;
        }
        return Math.min(pageSize, 200L);
    }
}
