package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.FeeItem;
import com.smart.property.property.dto.FeeItemDTO;
import com.smart.property.property.dto.LadderConfigDTO;
import com.smart.property.property.vo.FeeItemVO;

import java.util.List;

/**
 * 费项服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface FeeItemService extends IService<FeeItem> {

    PageResult<FeeItemVO> getFeeItemPage(PageQuery query, Long companyId, Long communityId);

    List<FeeItemVO> getFeeItemsByCommunityId(Long communityId, Long companyId);

    FeeItemVO getFeeItemById(Long id, Long companyId);

    void createFeeItem(FeeItemDTO dto, List<LadderConfigDTO> ladders, Long companyId, String operator);

    void updateFeeItem(Long id, FeeItemDTO dto, List<LadderConfigDTO> ladders, Long companyId, String operator);

    void deleteFeeItem(Long id, Long companyId);

    void toggleActive(Long id, Integer isActive, Long companyId, String operator);
}
