package com.smart.property.operation.remote;

import com.smart.property.common.core.domain.Result;
import com.smart.property.operation.remote.dto.FileRemoteUploadVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务远程客户端（operation → system）
 *
 * @author zzz
 * @since 2026-07-27
 */
@FeignClient(
        name = "smart-property-system",
        contextId = "fileRemoteClient-operation"
)
public interface FileRemoteClient {

    @PostMapping(value = "/api/v1/file/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileRemoteUploadVO> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "businessType", required = false, defaultValue = "default") String businessType,
            @RequestParam(value = "businessId", required = false) Long businessId);
}
