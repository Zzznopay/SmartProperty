package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.property.domain.Unit;
import com.smart.property.property.dto.UnitDTO;
import com.smart.property.property.vo.UnitVO;

import java.util.List;

/**
 * 单元服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface UnitService extends IService<Unit> {

    List<UnitVO> getUnitsByBuildingId(Long buildingId, Long companyId);

    Page<UnitVO> getUnitPage(Page<Unit> page,
                             Long companyId,
                             Long buildingId,
                             String unitCode,
                             String unitName,
                             Integer status);

    UnitVO getUnitById(Long id, Long companyId);

    void createUnit(UnitDTO dto, Long companyId, String operator);

    void updateUnit(Long id, UnitDTO dto, Long companyId, String operator);

    void deleteUnit(Long id, Long companyId);
}