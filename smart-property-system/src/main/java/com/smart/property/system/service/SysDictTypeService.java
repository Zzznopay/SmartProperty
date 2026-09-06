package com.smart.property.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.domain.SysDictType;
import com.smart.property.system.dto.SysDictTypeDTO;
import com.smart.property.system.vo.SysDictTypeVO;

public interface SysDictTypeService extends IService<SysDictType> {
    PageResult<SysDictTypeVO> getDictTypePage(PageQuery query, Long companyId);
    SysDictTypeVO getDictTypeById(Long id);
    void createDictType(SysDictTypeDTO dto, Long companyId, String operator);
    void updateDictType(Long id, SysDictTypeDTO dto, String operator);
}