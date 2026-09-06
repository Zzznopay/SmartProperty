package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.core.util.PageUtils;
import com.smart.property.system.convert.FileFolderConverter;
import com.smart.property.system.domain.FileFolder;
import com.smart.property.system.dto.FileFolderDTO;
import com.smart.property.system.mapper.FileFolderMapper;
import com.smart.property.system.service.FileFolderService;
import com.smart.property.system.vo.FileFolderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileFolderServiceImpl extends ServiceImpl<FileFolderMapper, FileFolder> implements FileFolderService {

    private final FileFolderConverter fileFolderConverter;

    @Override
    public List<FileFolderVO> getFolders(Long parentId, Long userId, Long companyId) {
        List<FileFolder> records = baseMapper.selectList(
                new LambdaQueryWrapper<FileFolder>()
                        .eq(FileFolder::getCompanyId, companyId)
                        .eq(FileFolder::getUserId, userId)
                        .eq(parentId != null, FileFolder::getParentId, parentId)
                        .orderByDesc(FileFolder::getCreateTime)
        );
        return fileFolderConverter.toVOList(records);
    }

    @Override
    public PageResult<FileFolderVO> getFoldersPage(PageQuery query, Long parentId, Long userId, Long companyId) {
        Page<FileFolder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FileFolder> wrapper = new LambdaQueryWrapper<FileFolder>()
                .eq(FileFolder::getCompanyId, companyId)
                .eq(FileFolder::getUserId, userId)
                .eq(parentId != null, FileFolder::getParentId, parentId)
                .orderByDesc(FileFolder::getCreateTime);
        Page<FileFolder> result = baseMapper.selectPage(page, wrapper);
        return PageUtils.toPage(result, fileFolderConverter::toVO);
    }

    @Override
    public FileFolderVO getById(Long id) {
        FileFolder folder = baseMapper.selectById(id);
        if (folder == null) throw new BusinessException("文件夹不存在");
        return fileFolderConverter.toVO(folder);
    }

    @Override
    public void createFolder(FileFolderDTO dto, Long companyId, Long userId, String operator) {
        FileFolder folder = fileFolderConverter.toEntity(dto);
        folder.setCompanyId(companyId);
        folder.setUserId(userId);
        folder.setCreateBy(operator);
        save(folder);
    }

    @Override
    public void updateFolder(Long id, FileFolderDTO dto, String operator) {
        FileFolder folder = fileFolderConverter.toEntity(dto);
        folder.setId(id);
        folder.setUpdateBy(operator);
        updateById(folder);
    }
}
