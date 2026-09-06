package com.smart.property.common.log.aspect;

import com.smart.property.common.log.annotation.OperLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 操作日志切面
 *
 * @author zzz
 * @since 2026-07-25
 */
@Slf4j
@Aspect
@Component
public class OperLogAspect {

    @Around("@annotation(operLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperLog operLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        String errorMsg = null;
        int status = 1;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            status = 0;
            errorMsg = e.getMessage();
            throw e;
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getDeclaringTypeName() + "." + signature.getName();

            log.info("操作日志 - 模块: {}, 操作: {}, 方法: {}, 耗时: {}ms, 状态: {}",
                    operLog.module(), operLog.description(), methodName, costTime, status);

            if (errorMsg != null) {
                log.warn("操作异常: {}", errorMsg);
            }
        }
    }
}
