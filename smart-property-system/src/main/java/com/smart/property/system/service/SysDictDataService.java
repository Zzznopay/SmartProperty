package com.smart.property.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.domain.SysDictData;
import com.smart.property.system.dto.SysDictDataDTO;
import com.smart.property.system.vo.SysDictDataVO;

import java.util.List;

public interface SysDictDataService extends IService<SysDictData> {
    PageResult<SysDictDataVO> getDictDataPage(PageQuery query, String dictType, Long companyId);
    List<SysDictDataVO> listByDictType(String dictType);
    SysDictDataVO getDictDataById(Long id);
    void createDictData(SysDictDataDTO dto, Long companyId, String operator);
    void updateDictData(Long id, SysDictDataDTO dto, String operator);
}