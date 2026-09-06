package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Blueprint;
import com.smart.property.property.dto.BlueprintDTO;
import com.smart.property.property.vo.BlueprintVO;

/**
 * 图纸服务接口
 *
 * @author zzz
 * @since 2026-07-28
 */
public interface BlueprintService extends IService<Blueprint> {

    PageResult<BlueprintVO> getBlueprintPage(PageQuery query, Long companyId, Long communityId, Long buildingId, Integer blueprintType);

    void createBlueprint(BlueprintDTO dto, Long companyId, String operator);
}