package com.smart.property.common.oss.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

/**
 * 文件路径分类器
 * <p>
 * 根据 businessType 把文件放到 MinIO 桶的对应子目录，便于后续运维与生命周期管理。
 * 路径格式：{prefix}/{companyId}[/{userId}]/{yyyyMM}/{uuid}.{ext}
 *
 * @author zzz
 * @since 2026-07-27
 */
public final class FilePathClassifier {

    private FilePathClassifier() {
    }

    private static final DateTimeFormatter YM = DateTimeFormatter.ofPattern("yyyyMM");

    /**
     * 允许的 businessType 白名单（防止任意输入导致桶内污染）
     */
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "avatar", "user-avatar", "company-logo",
            "notice", "order", "blueprint",
            "committee-photo", "regulation", "opinion",
            "vehicle", "netdisk", "report", "default"
    );

    /**
     * 校验 businessType：必须非空、不含 .. 或 /、长度 ≤ 64
     */
    public static boolean isValidBusinessType(String businessType) {
        if (businessType == null || businessType.isBlank() || businessType.length() > 64) {
            return false;
        }
        if (businessType.contains("..") || businessType.contains("/") || businessType.contains("\\")) {
            return false;
        }
        return ALLOWED_TYPES.contains(businessType);
    }

    /**
     * 根据 businessType 生成对象 key。
     *
     * @param businessType     业务类型
     * @param companyId        租户 ID
     * @param userId           用户 ID（部分类型需要，可为 null）
     * @param originalFilename 原始文件名（用于扩展名）
     * @return MinIO 对象 key
     */
    public static String classify(String businessType, Long companyId, Long userId, String originalFilename) {
        String type = isValidBusinessType(businessType) ? businessType : "default";
        String ext = FileTypeDetector.extOf(originalFilename);
        String suffix = ext.isEmpty() ? "" : "." + ext;
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String yyyymm = LocalDate.now().format(YM);

        String prefix;
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case "avatar", "user-avatar" -> prefix = "avatar";
            case "company-logo" -> prefix = "logo";
            case "notice" -> prefix = "notice";
            case "order" -> prefix = "order";
            case "blueprint" -> prefix = "blueprint";
            case "committee-photo" -> prefix = "committee";
            case "regulation" -> prefix = "regulation";
            case "opinion" -> prefix = "opinion";
            case "vehicle" -> prefix = "vehicle";
            case "netdisk" -> prefix = "netdisk";
            case "report" -> prefix = "report";
            default -> prefix = "default";
        }

        sb.append(prefix).append('/').append(companyId == null ? 0 : companyId);
        // avatar 类需要 userId 维度
        if ("avatar".equals(type) || "user-avatar".equals(type) || "netdisk".equals(type)) {
            if (userId != null) {
                sb.append('/').append(userId);
            }
        }
        // committee-photo / company-logo 不按月份分
        if ("committee-photo".equals(type) || "company-logo".equals(type)) {
            sb.append('/').append(uuid).append(suffix);
        } else {
            sb.append('/').append(yyyymm).append('/').append(uuid).append(suffix);
        }
        return sb.toString();
    }
}
