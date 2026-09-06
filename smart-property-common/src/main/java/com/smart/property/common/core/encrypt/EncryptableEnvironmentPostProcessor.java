package com.smart.property.common.core.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 敏感配置解密后处理器
 * <p>
 * 在 Spring 环境准备阶段扫描所有属性源，将 {@code ENC(密文)} 形式的属性值就地解密，
 * 解密结果以更高优先级的属性源注入，使下游 Bean 通过 {@code @Value} 注入时拿到明文。
 * <p>
 * 通过 {@code META-INF/spring.factories} 注册，无需组件扫描即可生效。
 *
 * @author zzz
 * @since 2026-07-28
 */
@Slf4j
public class EncryptableEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String DECRYPTED_SUFFIX = "-decrypted";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        boolean usingDefaultKey = System.getenv(ConfigCrypto.ENV_KEY) == null
                || System.getenv(ConfigCrypto.ENV_KEY).isBlank();
        if (usingDefaultKey) {
            log.warn("未设置环境变量 {}，配置解密使用开发默认密钥，生产环境请务必通过环境变量覆盖。",
                    ConfigCrypto.ENV_KEY);
        }

        List<DecryptedSource> decrypted = new ArrayList<>();
        for (PropertySource<?> ps : environment.getPropertySources()) {
            if (!(ps instanceof EnumerablePropertySource<?> source)) {
                continue;
            }
            Map<String, Object> overrides = new HashMap<>();
            for (String name : source.getPropertyNames()) {
                Object value = source.getProperty(name);
                if (value instanceof String str && ConfigCrypto.isEncrypted(str)) {
                    try {
                        overrides.put(name, ConfigCrypto.decrypt(str));
                    } catch (Exception e) {
                        log.error("配置项 {} 解密失败: {}", name, e.getMessage());
                    }
                }
            }
            if (!overrides.isEmpty()) {
                decrypted.add(new DecryptedSource(ps.getName(), overrides));
            }
        }

        // 在原属性源之前插入解密结果，优先级高于原密文
        for (DecryptedSource d : decrypted) {
            environment.getPropertySources()
                    .addBefore(d.originName, new MapPropertySource(d.originName + DECRYPTED_SUFFIX, d.values));
        }
    }

    private record DecryptedSource(String originName, Map<String, Object> values) {
    }
}
