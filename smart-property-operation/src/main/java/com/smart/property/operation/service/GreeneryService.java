package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.Greenery;
import com.smart.property.operation.dto.GreeneryDTO;
import com.smart.property.operation.vo.GreeneryVO;

/**
 * 绿化植被服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface GreeneryService extends IService<Greenery> {

    PageResult<GreeneryVO> getGreeneryPage(PageQuery query, Long companyId, Long communityId, Integer greeneryType);

    void createGreenery(GreeneryDTO dto, Long companyId, String operator);

    void updateGreenery(Long id, GreeneryDTO dto, String operator);
}