package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.system.convert.SysDictDataConverter;
import com.smart.property.system.domain.SysDictData;
import com.smart.property.system.dto.SysDictDataDTO;
import com.smart.property.system.mapper.SysDictDataMapper;
import com.smart.property.system.service.SysDictDataService;
import com.smart.property.system.vo.SysDictDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    private final SysDictDataConverter sysDictDataConverter;

    @Override
    public PageResult<SysDictDataVO> getDictDataPage(PageQuery query, String dictType, Long companyId) {
        Page<SysDictData> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<SysDictData>()
                .eq(dictType != null, SysDictData::getDictType, dictType)
                .orderByAsc(SysDictData::getSort);
        Page<SysDictData> result = baseMapper.selectPage(page, wrapper);
        List<SysDictDataVO> records = sysDictDataConverter.toVOList(result.getRecords());
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public List<SysDictDataVO> listByDictType(String dictType) {
        List<SysDictData> entities = baseMapper.selectList(
                new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getDictType, dictType)
                        .orderByAsc(SysDictData::getSort)
        );
        return sysDictDataConverter.toVOList(entities);
    }

    @Override
    public SysDictDataVO getDictDataById(Long id) {
        SysDictData d = getById(id);
        if (d == null) throw new BusinessException("字典数据不存在");
        return sysDictDataConverter.toVO(d);
    }

    @Override
    public void createDictData(SysDictDataDTO dto, Long companyId, String operator) {
        SysDictData data = sysDictDataConverter.toEntity(dto);
        data.setCreateBy(operator);
        save(data);
    }

    @Override
    public void updateDictData(Long id, SysDictDataDTO dto, String operator) {
        SysDictData existing = getById(id);
        if (existing == null) throw new BusinessException("字典数据不存在");
        SysDictData data = sysDictDataConverter.toEntity(dto);
        data.setId(id);
        data.setCreateBy(existing.getCreateBy());
        data.setCreateTime(existing.getCreateTime());
        data.setUpdateBy(operator);
        updateById(data);
    }
}