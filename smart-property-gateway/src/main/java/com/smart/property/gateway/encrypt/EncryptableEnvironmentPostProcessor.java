package com.smart.property.gateway.encrypt;

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
 * 敏感配置解密后处理器（网关副本）。
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

        for (DecryptedSource d : decrypted) {
            environment.getPropertySources()
                    .addBefore(d.originName, new MapPropertySource(d.originName + DECRYPTED_SUFFIX, d.values));
        }
    }

    private record DecryptedSource(String originName, Map<String, Object> values) {
    }
}
