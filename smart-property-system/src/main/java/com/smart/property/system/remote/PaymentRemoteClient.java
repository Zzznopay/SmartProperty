package com.smart.property.system.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 跨服务调用 property 财务数据。fallback 见 PaymentRemoteFallbackFactory。
 *
 * @author zzz
 * @since 2026-07-28
 */
@FeignClient(name = "smart-property-property", fallbackFactory = PaymentRemoteFallbackFactory.class)
public interface PaymentRemoteClient {

    @GetMapping("/api/v1/finance/payments/statistics")
    Map<String, Object> statistics(@RequestParam Long companyId,
                                    @RequestParam String startMonth,
                                    @RequestParam String endMonth);
}
