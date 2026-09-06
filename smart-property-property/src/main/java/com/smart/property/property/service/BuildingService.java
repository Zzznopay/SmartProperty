package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.property.domain.Building;
import com.smart.property.property.dto.BuildingDTO;
import com.smart.property.property.vo.BuildingVO;

import java.util.List;

/**
 * 楼宇服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface BuildingService extends IService<Building> {

    List<BuildingVO> getBuildingsByCommunityId(Long communityId, Long companyId);

    Page<BuildingVO> getBuildingPage(Page<Building> page,
                                     Long companyId,
                                     Long communityId,
                                     String buildingCode,
                                     String buildingName,
                                     Integer status);

    BuildingVO getBuildingById(Long id, Long companyId);

    void createBuilding(BuildingDTO dto, Long companyId, String operator);

    void updateBuilding(Long id, BuildingDTO dto, Long companyId, String operator);

    void deleteBuilding(Long id, Long companyId);
}