package com.smart.property.gateway.encrypt;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;

import java.nio.charset.StandardCharsets;

/**
 * 配置文件加解密工具（网关副本）
 * <p>
 * 网关为 WebFlux 响应式服务，不依赖 common 模块，因此独立维护一份解密逻辑。
 * 主密钥来自环境变量 {@code SMART_PROPERTY_SECRET_KEY}，与后端服务保持一致。
 *
 * @author zzz
 * @since 2026-07-28
 */
public final class ConfigCrypto {

    private ConfigCrypto() {
    }

    public static final String ENV_KEY = "SMART_PROPERTY_SECRET_KEY";
    public static final String DEFAULT_KEY = "smart-property-dev-secret-2026";
    public static final String PREFIX = "ENC(";
    public static final String SUFFIX = ")";

    public static String masterKey() {
        String key = System.getProperty(ENV_KEY);
        if (key == null || key.isBlank()) {
            key = System.getenv(ENV_KEY);
        }
        return (key == null || key.isBlank()) ? DEFAULT_KEY : key;
    }

    public static boolean isEncrypted(String value) {
        return value != null
                && value.startsWith(PREFIX)
                && value.endsWith(SUFFIX)
                && value.length() > PREFIX.length() + SUFFIX.length();
    }

    public static String encrypt(String plain) {
        if (plain == null) {
            return null;
        }
        return PREFIX + aes().encryptBase64(plain) + SUFFIX;
    }

    public static String decrypt(String value) {
        if (!isEncrypted(value)) {
            return value;
        }
        String body = value.substring(PREFIX.length(), value.length() - SUFFIX.length());
        return aes().decryptStr(body);
    }

    private static AES aes() {
        byte[] keyBytes = DigestUtil.sha256(masterKey().getBytes(StandardCharsets.UTF_8));
        return new AES(keyBytes);
    }
}
