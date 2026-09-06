package com.smart.property.system.service.helper;

import cn.hutool.core.io.FileUtil;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.oss.service.MinioService;
import com.smart.property.common.oss.util.FilePathClassifier;
import com.smart.property.common.oss.util.FileTypeDetector;
import com.smart.property.system.domain.FileInfo;
import com.smart.property.system.mapper.FileInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 文件路径解析 + 上传到 MinIO + 写库
 *
 * @author zzz
 * @since 2026-07-27
 */
@Component
@RequiredArgsConstructor
public class FilePathResolver {

    private final MinioService minioService;
    private final FileInfoMapper fileInfoMapper;

    @Value("${file.url.base:http://localhost:9000}")
    private String urlBase;

    /**
     * 上传 MultipartFile 到 MinIO 并插入 file_info 行
     *
     * @return fileUrl（已组装）
     */
    public FileInfo uploadAndSave(MultipartFile file,
                                  String businessType,
                                  Long businessId,
                                  Long companyId,
                                  Long userId,
                                  String userName) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        if (!FilePathClassifier.isValidBusinessType(businessType)) {
            throw new BusinessException("非法 businessType: " + businessType);
        }
        String original = file.getOriginalFilename() == null ? "unknown" : file.getOriginalFilename();
        String ext = FileTypeDetector.extOf(original);
        String mime = FileTypeDetector.mimeOf(original);
        String key = FilePathClassifier.classify(businessType, companyId, userId, original);

        String md5;
        try (InputStream in = file.getInputStream()) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int n;
            // 计算 MD5 同时把 InputStream 全部读入临时流以再次传给 MinIO 不可行 —— 用 transferTo 中转
            // 这里采用：先转存到临时文件，再读两次（一次 MD5、一次上传）
            java.nio.file.Path tmp = java.nio.file.Files.createTempFile("upload-", "." + (ext.isEmpty() ? "bin" : ext));
            file.transferTo(tmp.toFile());
            md5 = computeMd5(tmp);
            try (InputStream uploadIn = java.nio.file.Files.newInputStream(tmp)) {
                minioService.putObject(minioService.getBucketName(), key, uploadIn, java.nio.file.Files.size(tmp), mime);
            }
            java.nio.file.Files.deleteIfExists(tmp);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new BusinessException("文件处理失败: " + e.getMessage(), e);
        }

        FileInfo info = new FileInfo();
        info.setCompanyId(companyId);
        info.setFileName(FileUtil.getName(original));
        info.setOriginalName(original);
        info.setFilePath("/" + key);
        info.setFileUrl(urlBase + "/" + minioService.getBucketName() + "/" + key);
        info.setFileSize(file.getSize());
        info.setFileType(mime);
        info.setFileExt(ext.isEmpty() ? "bin" : ext);
        info.setMd5(md5);
        info.setStorageType(1); // MinIO
        info.setBucketName(minioService.getBucketName());
        info.setBusinessType(businessType);
        info.setBusinessId(businessId);
        info.setUploadUserId(userId);
        info.setUploadUserName(userName);
        info.setDownloadCount(0);
        info.setStatus(1);
        fileInfoMapper.insert(info);
        return info;
    }

    /**
     * 把一个已存在的临时文件上传到 MinIO 并写库（用于分片合并后的产物）
     */
    public FileInfo uploadTempFileAndSave(java.nio.file.Path mergedFile,
                                          String businessType,
                                          Long businessId,
                                          String fileName,
                                          String md5,
                                          String mime,
                                          String ext,
                                          Long companyId,
                                          Long userId,
                                          String userName) {
        if (!FilePathClassifier.isValidBusinessType(businessType)) {
            throw new BusinessException("非法 businessType: " + businessType);
        }
        String key = FilePathClassifier.classify(businessType, companyId, userId, fileName);
        try (InputStream in = java.nio.file.Files.newInputStream(mergedFile)) {
            minioService.putObject(minioService.getBucketName(), key, in, java.nio.file.Files.size(mergedFile), mime);
        } catch (IOException e) {
            throw new BusinessException("上传 MinIO 失败: " + e.getMessage(), e);
        }

        FileInfo info = new FileInfo();
        info.setCompanyId(companyId);
        info.setFileName(FileUtil.getName(fileName));
        info.setOriginalName(fileName);
        info.setFilePath("/" + key);
        info.setFileUrl(urlBase + "/" + minioService.getBucketName() + "/" + key);
        try {
            info.setFileSize(java.nio.file.Files.size(mergedFile));
        } catch (IOException e) {
            throw new BusinessException("读取合并文件大小失败: " + e.getMessage(), e);
        }
        info.setFileType(mime);
        info.setFileExt(ext.isEmpty() ? "bin" : ext);
        info.setMd5(md5);
        info.setStorageType(1);
        info.setBucketName(minioService.getBucketName());
        info.setBusinessType(businessType);
        info.setBusinessId(businessId);
        info.setUploadUserId(userId);
        info.setUploadUserName(userName);
        info.setDownloadCount(0);
        info.setStatus(1);
        fileInfoMapper.insert(info);
        return info;
    }

    private String computeMd5(java.nio.file.Path file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (InputStream in = java.nio.file.Files.newInputStream(file)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) {
                md.update(buf, 0, n);
            }
        }
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
