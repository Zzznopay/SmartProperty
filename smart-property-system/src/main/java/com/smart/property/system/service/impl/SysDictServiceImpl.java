package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.property.system.convert.SysDictDataConverter;
import com.smart.property.system.convert.SysDictTypeConverter;
import com.smart.property.system.domain.SysDictData;
import com.smart.property.system.domain.SysDictType;
import com.smart.property.system.mapper.SysDictDataMapper;
import com.smart.property.system.mapper.SysDictTypeMapper;
import com.smart.property.system.service.SysDictService;
import com.smart.property.system.vo.SysDictDataVO;
import com.smart.property.system.vo.SysDictTypeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典服务实现
 *
 * @author zzz
 * @since 2026-07-25
 */
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictService {

    private final SysDictDataMapper dictDataMapper;
    private final SysDictTypeConverter sysDictTypeConverter;
    private final SysDictDataConverter sysDictDataConverter;

    @Override
    public List<SysDictTypeVO> getDictTypeList() {
        List<SysDictType> records = baseMapper.selectList(
                new LambdaQueryWrapper<SysDictType>()
                        .eq(SysDictType::getStatus, 1)
                        .eq(SysDictType::getIsDeleted, 0)
                        .orderByAsc(SysDictType::getId)
        );
        return sysDictTypeConverter.toVOList(records);
    }

    @Override
    public List<SysDictDataVO> getDictDataByType(String dictType) {
        List<SysDictData> records = dictDataMapper.selectList(
                new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getDictType, dictType)
                        .eq(SysDictData::getStatus, 1)
                        .orderByAsc(SysDictData::getSort)
        );
        return sysDictDataConverter.toVOList(records);
    }
}
