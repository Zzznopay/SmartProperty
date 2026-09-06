package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.system.convert.SysDictTypeConverter;
import com.smart.property.system.domain.SysDictType;
import com.smart.property.system.dto.SysDictTypeDTO;
import com.smart.property.system.mapper.SysDictTypeMapper;
import com.smart.property.system.service.SysDictTypeService;
import com.smart.property.system.vo.SysDictTypeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    private final SysDictTypeConverter sysDictTypeConverter;

    @Override
    public PageResult<SysDictTypeVO> getDictTypePage(PageQuery query, Long companyId) {
        Page<SysDictType> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<SysDictType>()
                .orderByAsc(SysDictType::getDictType);
        Page<SysDictType> result = baseMapper.selectPage(page, wrapper);
        List<SysDictTypeVO> records = sysDictTypeConverter.toVOList(result.getRecords());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public SysDictTypeVO getDictTypeById(Long id) {
        SysDictType t = getById(id);
        if (t == null) throw new BusinessException("字典类型不存在");
        return sysDictTypeConverter.toVO(t);
    }

    @Override
    public void createDictType(SysDictTypeDTO dto, Long companyId, String operator) {
        SysDictType type = sysDictTypeConverter.toEntity(dto);
        type.setCreateBy(operator);
        save(type);
    }

    @Override
    public void updateDictType(Long id, SysDictTypeDTO dto, String operator) {
        SysDictType existing = getById(id);
        if (existing == null) throw new BusinessException("字典类型不存在");
        SysDictType type = sysDictTypeConverter.toEntity(dto);
        type.setId(id);
        type.setCreateBy(existing.getCreateBy());
        type.setCreateTime(existing.getCreateTime());
        type.setUpdateBy(operator);
        updateById(type);
    }
}