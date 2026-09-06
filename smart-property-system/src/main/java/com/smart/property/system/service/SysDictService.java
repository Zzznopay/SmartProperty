package com.smart.property.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.system.domain.SysDictType;
import com.smart.property.system.vo.SysDictDataVO;
import com.smart.property.system.vo.SysDictTypeVO;

import java.util.List;

/**
 * 字典服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface SysDictService extends IService<SysDictType> {

    /**
     * 查询字典类型列表
     */
    List<SysDictTypeVO> getDictTypeList();

    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictDataVO> getDictDataByType(String dictType);
}
