package com.smart.property.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关服务启动类
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
