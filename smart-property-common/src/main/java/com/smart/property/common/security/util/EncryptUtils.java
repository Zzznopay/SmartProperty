package com.smart.property.common.security.util;

import cn.hutool.crypto.symmetric.AES;
import com.smart.property.common.core.encrypt.ConfigCrypto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 加密工具类
 *
 * <p>密钥来自 {@code encrypt.key} 配置。配置允许以 {@code ENC(密文)} 形式存放
 * （由 {@link ConfigCrypto} 在读取时解密）——不能依赖 EnvironmentPostProcessor
 * 提前解密：application-common.yml 走 spring.config.import 在 ConfigData 阶段加载，
 * 晚于 EnvironmentPostProcessor 执行，因此这里在首次使用时自行解密。</p>
 *
 * @author zzz
 * @since 2026-07-25
 */
@Component
public class EncryptUtils {

    @Value("${encrypt.key:1234567890abcdef}")
    private String encryptedKey;

    private volatile AES aes;

    private AES getAes() {
        if (aes == null) {
            synchronized (this) {
                if (aes == null) {
                    String key = ConfigCrypto.decrypt(encryptedKey);
                    aes = new AES(key.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        return aes;
    }

    public String encrypt(String content) {
        if (content == null) {
            return null;
        }
        return getAes().encryptBase64(content);
    }

    public String decrypt(String encryptStr) {
        if (encryptStr == null) {
            return null;
        }
        return getAes().decryptStr(encryptStr);
    }
}
