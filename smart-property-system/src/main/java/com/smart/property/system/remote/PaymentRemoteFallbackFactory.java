package com.smart.property.system.remote;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * PaymentRemoteClient 的 fallback 工厂：远端不可达时返回空 Map 兜底，
 * 避免阻塞 system 服务启动，也避免页面在远端故障时直接 500。
 *
 * @author zzz
 * @since 2026-07-28
 */
@Slf4j
@Component
public class PaymentRemoteFallbackFactory implements FallbackFactory<PaymentRemoteClient> {

    @Override
    public PaymentRemoteClient create(Throwable cause) {
        log.warn("PaymentRemoteClient 不可用，使用空结果兜底: {}", cause.getMessage());
        return (companyId, startMonth, endMonth) -> Collections.emptyMap();
    }
}
