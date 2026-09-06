package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.GoodsRecord;
import com.smart.property.operation.dto.GoodsRecordDTO;
import com.smart.property.operation.vo.GoodsRecordVO;

/**
 * 物品出入服务接口
 *
 * @author zzz
 * @since 2026-07-28
 */
public interface GoodsRecordService extends IService<GoodsRecord> {

    PageResult<GoodsRecordVO> getPage(PageQuery query, Long companyId, Long communityId);

    GoodsRecordVO getById(Long id);

    void create(GoodsRecordDTO dto, Long companyId, String operator);
}