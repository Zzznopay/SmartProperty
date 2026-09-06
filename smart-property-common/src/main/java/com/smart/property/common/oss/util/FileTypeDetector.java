package com.smart.property.common.oss.util;

import cn.hutool.core.io.FileUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 文件类型检测工具
 *
 * @author zzz
 * @since 2026-07-27
 */
public final class FileTypeDetector {

    private FileTypeDetector() {
    }

    /**
     * 提取文件扩展名（小写，不含点）
     */
    public static String extOf(String filename) {
        if (filename == null) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return "";
        }
        return filename.substring(dot + 1).toLowerCase();
    }

    /**
     * 探测 MIME 类型（优先用 Files.probeContentType，回退到 Hutool）
     */
    public static String mimeOf(String filename) {
        if (filename == null || filename.isBlank()) {
            return "application/octet-stream";
        }
        try {
            Path temp = Files.createTempFile("mime-detect-", "." + extOf(filename));
            String type = Files.probeContentType(temp);
            Files.deleteIfExists(temp);
            if (type != null) {
                return type;
            }
        } catch (IOException ignored) {
        }
        String type = FileUtil.getMimeType(filename);
        return type != null ? type : "application/octet-stream";
    }

    /**
     * 校验 MultipartFile 大小
     *
     * @return true 表示在限制范围内
     */
    public static boolean isWithinLimit(MultipartFile file, long maxBytes) {
        return file != null && file.getSize() > 0 && file.getSize() <= maxBytes;
    }
}
