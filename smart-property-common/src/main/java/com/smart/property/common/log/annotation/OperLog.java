package com.smart.property.common.log.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author zzz
 * @since 2026-07-25
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    /** 模块名称 */
    String module() default "";

    /** 业务类型(0其它 1新增 2修改 3删除 4查询 5导出) */
    int businessType() default 0;

    /** 操作描述 */
    String description() default "";
}
