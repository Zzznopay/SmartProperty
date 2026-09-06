package com.smart.property.system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API文档配置
 *
 * @author zzz
 * @since 2026-07-25
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智能物业系统 - 系统基础服务")
                        .version("1.0.0")
                        .description("和家云服务管理云平台 API文档")
                        .contact(new Contact()
                                .name("zzz")));
    }
}
