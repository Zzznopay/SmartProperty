package com.smart.property.system.service.helper;

import com.smart.property.system.convert.FileInfoConverter;
import com.smart.property.system.domain.FileInfo;
import com.smart.property.system.mapper.FileInfoMapper;
import com.smart.property.system.vo.FileInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 秒传辅助：按 (companyId, businessType, md5) 查重
 *
 * @author zzz
 * @since 2026-07-27
 */
@Component
@RequiredArgsConstructor
public class Md5Deduper {

    private final FileInfoMapper fileInfoMapper;
    private final FileInfoConverter fileInfoConverter;

    /**
     * @return 已存在则返回 FileInfoVO（命中秒传），否则 null
     */
    public FileInfoVO tryDedup(Long companyId, String businessType, String md5) {
        if (companyId == null || md5 == null || md5.isBlank()) {
            return null;
        }
        String bt = businessType == null || businessType.isBlank() ? "default" : businessType;
        FileInfo existing = fileInfoMapper.selectByMd5AndType(companyId, bt, md5);
        if (existing == null) {
            return null;
        }
        return fileInfoConverter.toVO(existing);
    }
}
