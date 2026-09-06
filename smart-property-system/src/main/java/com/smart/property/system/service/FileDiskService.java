package com.smart.property.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.domain.FileDisk;
import com.smart.property.system.dto.FileDiskDTO;
import com.smart.property.system.vo.FileDiskVO;

import java.util.List;

public interface FileDiskService extends IService<FileDisk> {
    /** 兼容旧版：全量返回 */
    List<FileDiskVO> getDiskFiles(Long folderId, Long userId, Long companyId);

    /** 分页查询（推荐） */
    PageResult<FileDiskVO> getDiskFilesPage(PageQuery query, Long folderId, Long userId, Long companyId);

    FileDiskVO getById(Long id);
    void createFile(FileDiskDTO dto, Long companyId, Long userId, String operator);
    void updateFile(Long id, FileDiskDTO dto, String operator);
    void shareFile(Long id, List<Long> shareUserIds, String operator);
}
