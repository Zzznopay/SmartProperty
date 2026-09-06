package com.smart.property.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.domain.FileFolder;
import com.smart.property.system.dto.FileFolderDTO;
import com.smart.property.system.vo.FileFolderVO;

import java.util.List;

public interface FileFolderService extends IService<FileFolder> {
    /** 兼容旧版：全量返回（保留作内部调用） */
    List<FileFolderVO> getFolders(Long parentId, Long userId, Long companyId);

    /** 分页查询（推荐） */
    PageResult<FileFolderVO> getFoldersPage(PageQuery query, Long parentId, Long userId, Long companyId);

    FileFolderVO getById(Long id);
    void createFolder(FileFolderDTO dto, Long companyId, Long userId, String operator);
    void updateFolder(Long id, FileFolderDTO dto, String operator);
}
