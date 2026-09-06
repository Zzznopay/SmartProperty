package com.smart.property.common.core.encrypt;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;

import java.nio.charset.StandardCharsets;

/**
 * 配置文件加解密工具
 * <p>
 * 用于对 yml 中的敏感信息（数据库密码、Redis 密码、MQ 密码、MinIO 密钥、JWT 密钥等）进行对称加密。
 * 密文以 {@code ENC(密文)} 形式存放于配置文件，启动时由
 * {@link EncryptableEnvironmentPostProcessor} 自动解密。
 * <p>
 * 主密钥来自环境变量 {@link #ENV_KEY}，未设置时使用开发默认密钥（仅用于本地开发，
 * 生产环境务必通过环境变量覆盖）。
 *
 * @author zzz
 * @since 2026-07-28
 */
public final class ConfigCrypto {

    private ConfigCrypto() {
    }

    /** 主密钥环境变量名 */
    public static final String ENV_KEY = "SMART_PROPERTY_SECRET_KEY";

    /** 开发环境默认主密钥（生产环境请通过环境变量覆盖） */
    public static final String DEFAULT_KEY = "smart-property-dev-secret-2026";

    /** 密文前缀 */
    public static final String PREFIX = "ENC(";

    /** 密文后缀 */
    public static final String SUFFIX = ")";

    /**
     * 获取主密钥：优先系统属性 (-D)、其次环境变量，缺省使用开发默认值
     */
    public static String masterKey() {
        String key = System.getProperty(ENV_KEY);
        if (key == null || key.isBlank()) {
            key = System.getenv(ENV_KEY);
        }
        return (key == null || key.isBlank()) ? DEFAULT_KEY : key;
    }

    /**
     * 是否为加密密文
     */
    public static boolean isEncrypted(String value) {
        return value != null
                && value.startsWith(PREFIX)
                && value.endsWith(SUFFIX)
                && value.length() > PREFIX.length() + SUFFIX.length();
    }

    /**
     * 加密明文，返回 {@code ENC(密文)}
     */
    public static String encrypt(String plain) {
        if (plain == null) {
            return null;
        }
        return PREFIX + aes().encryptBase64(plain) + SUFFIX;
    }

    /**
     * 解密 {@code ENC(密文)}；非密文原样返回
     */
    public static String decrypt(String value) {
        if (!isEncrypted(value)) {
            return value;
        }
        String body = value.substring(PREFIX.length(), value.length() - SUFFIX.length());
        return aes().decryptStr(body);
    }

    private static AES aes() {
        // 任意长度主密钥统一派生为 32 字节（AES-256），避免密钥长度受限
        byte[] keyBytes = DigestUtil.sha256(masterKey().getBytes(StandardCharsets.UTF_8));
        return new AES(keyBytes);
    }
}
