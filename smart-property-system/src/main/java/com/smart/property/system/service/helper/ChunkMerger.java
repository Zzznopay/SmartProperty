package com.smart.property.system.service.helper;

import com.smart.property.system.exception.ChunkIncompleteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Map;

import static com.smart.property.common.oss.constants.OssConstants.*;

/**
 * 分片合并 + MD5 校验
 *
 * @author zzz
 * @since 2026-07-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChunkMerger {

    private final StringRedisTemplate redis;

    @Value("${file.upload-tmp-dir:./tmp/upload}")
    private String tmpDir;

    /**
     * 合并分片到临时文件并流式校验 MD5
     *
     * @return 合并后临时文件路径
     */
    public Path merge(Long companyId, String md5, int totalChunks) {
        Path chunkDir = Paths.get(tmpDir, CHUNK_TMP_DIR, String.valueOf(companyId), md5);
        if (!Files.isDirectory(chunkDir)) {
            throw new ChunkIncompleteException("分片目录不存在: " + chunkDir);
        }

        Path merged = Paths.get(tmpDir, CHUNK_MERGED_DIR, md5 + ".tmp");
        try {
            Files.createDirectories(merged.getParent());
        } catch (IOException e) {
            throw new ChunkIncompleteException("无法创建合并目录: " + e.getMessage());
        }

        try (OutputStream out = Files.newOutputStream(merged)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            for (int i = 0; i < totalChunks; i++) {
                Path chunk = chunkDir.resolve(String.valueOf(i));
                if (!Files.exists(chunk)) {
                    throw new ChunkIncompleteException("缺少分片: index=" + i);
                }
                try (InputStream in = Files.newInputStream(chunk)) {
                    byte[] buf = new byte[8192];
                    int n;
                    while ((n = in.read(buf)) > 0) {
                        out.write(buf, 0, n);
                        digest.update(buf, 0, n);
                    }
                }
            }
            String actual = toHex(digest.digest());
            if (!actual.equalsIgnoreCase(md5)) {
                Files.deleteIfExists(merged);
                throw new ChunkIncompleteException("合并文件 MD5 校验失败: expected=" + md5 + ", actual=" + actual);
            }
            return merged;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new ChunkIncompleteException("分片合并失败: " + e.getMessage());
        }
    }

    /**
     * 清理分片临时目录和 Redis 状态
     */
    public void cleanup(Long companyId, String md5) {
        try {
            Path chunkDir = Paths.get(tmpDir, CHUNK_TMP_DIR, String.valueOf(companyId), md5);
            if (Files.exists(chunkDir)) {
                Files.walk(chunkDir)
                        .sorted((a, b) -> b.compareTo(a)) // 倒序：先删文件再删目录
                        .forEach(p -> {
                            try {
                                Files.deleteIfExists(p);
                            } catch (IOException ignored) {
                            }
                        });
            }
            Path merged = Paths.get(tmpDir, CHUNK_MERGED_DIR, md5 + ".tmp");
            Files.deleteIfExists(merged);
        } catch (IOException e) {
            log.warn("清理分片临时文件失败: {}", e.getMessage());
        }
        redis.delete(CHUNK_RECEIVED_KEY_PREFIX + md5);
        redis.delete(CHUNK_META_KEY_PREFIX + md5);
    }

    /**
     * 记录分片元数据（首片时）
     */
    public void recordMeta(String md5, String fileName, int totalChunks,
                           String businessType, Long companyId, Long uploadUserId, int ttlHours) {
        String key = CHUNK_META_KEY_PREFIX + md5;
        Map<String, String> data = Map.of(
                "fileName", fileName == null ? "" : fileName,
                "totalChunks", String.valueOf(totalChunks),
                "businessType", businessType == null ? "default" : businessType,
                "companyId", String.valueOf(companyId == null ? 0 : companyId),
                "uploadedBy", String.valueOf(uploadUserId == null ? 0 : uploadUserId),
                "status", "in-progress"
        );
        redis.opsForHash().putAll(key, data);
        redis.expire(key, Duration.ofHours(ttlHours));
    }

    public void markChunk(String md5, int index, int ttlHours) {
        String key = CHUNK_RECEIVED_KEY_PREFIX + md5;
        redis.opsForHash().put(key, String.valueOf(index), "1");
        redis.expire(key, Duration.ofHours(ttlHours));
    }

    public long receivedChunks(String md5) {
        String key = CHUNK_RECEIVED_KEY_PREFIX + md5;
        Long size = redis.opsForHash().size(key);
        return size == null ? 0 : size;
    }

    public boolean hasMeta(String md5) {
        return Boolean.TRUE.equals(redis.hasKey(CHUNK_META_KEY_PREFIX + md5));
    }

    public Path resolveChunkPath(Long companyId, String md5, int index) {
        return Paths.get(tmpDir, CHUNK_TMP_DIR, String.valueOf(companyId), md5, String.valueOf(index));
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
