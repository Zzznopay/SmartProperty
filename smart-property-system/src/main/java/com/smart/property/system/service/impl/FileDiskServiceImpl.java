package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.core.util.JsonUtils;
import com.smart.property.common.core.util.PageUtils;
import com.smart.property.system.convert.FileDiskConverter;
import com.smart.property.system.domain.FileDisk;
import com.smart.property.system.dto.FileDiskDTO;
import com.smart.property.system.mapper.FileDiskMapper;
import com.smart.property.system.service.FileDiskService;
import com.smart.property.system.vo.FileDiskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileDiskServiceImpl extends ServiceImpl<FileDiskMapper, FileDisk> implements FileDiskService {

    private final FileDiskConverter fileDiskConverter;

    @Override
    public List<FileDiskVO> getDiskFiles(Long folderId, Long userId, Long companyId) {
        List<FileDisk> records = baseMapper.selectList(
                new LambdaQueryWrapper<FileDisk>()
                        .eq(FileDisk::getCompanyId, companyId)
                        .eq(FileDisk::getUserId, userId)
                        .eq(folderId != null, FileDisk::getFolderId, folderId)
                        .orderByDesc(FileDisk::getCreateTime)
        );
        return fileDiskConverter.toVOList(records);
    }

    @Override
    public PageResult<FileDiskVO> getDiskFilesPage(PageQuery query, Long folderId, Long userId, Long companyId) {
        Page<FileDisk> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FileDisk> wrapper = new LambdaQueryWrapper<FileDisk>()
                .eq(FileDisk::getCompanyId, companyId)
                .eq(FileDisk::getUserId, userId)
                .eq(folderId != null, FileDisk::getFolderId, folderId)
                .orderByDesc(FileDisk::getCreateTime);
        Page<FileDisk> result = baseMapper.selectPage(page, wrapper);
        return PageUtils.toPage(result, fileDiskConverter::toVO);
    }

    @Override
    public FileDiskVO getById(Long id) {
        FileDisk disk = baseMapper.selectById(id);
        if (disk == null) throw new BusinessException("文件记录不存在");
        return fileDiskConverter.toVO(disk);
    }

    @Override
    public void createFile(FileDiskDTO dto, Long companyId, Long userId, String operator) {
        FileDisk disk = fileDiskConverter.toEntity(dto);
        disk.setCompanyId(companyId);
        disk.setUserId(userId);
        disk.setCreateBy(operator);
        save(disk);
    }

    @Override
    public void updateFile(Long id, FileDiskDTO dto, String operator) {
        FileDisk disk = fileDiskConverter.toEntity(dto);
        disk.setId(id);
        disk.setUpdateBy(operator);
        updateById(disk);
    }

    @Override
    public void shareFile(Long id, List<Long> shareUserIds, String operator) {
        FileDisk disk = baseMapper.selectById(id);
        if (disk == null) throw new BusinessException("文件记录不存在");
        disk.setIsShared(1);
        disk.setShareUserIds(JsonUtils.toJson(shareUserIds));
        disk.setUpdateBy(operator);
        updateById(disk);
    }
}