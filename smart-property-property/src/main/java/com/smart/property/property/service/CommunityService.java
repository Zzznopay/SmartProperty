package com.smart.property.property.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.property.domain.Community;
import com.smart.property.property.dto.CommunityDTO;
import com.smart.property.property.vo.CommunityVO;

/**
 * 小区服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface CommunityService extends IService<Community> {

    PageResult<CommunityVO> getCommunityPage(PageQuery query, Long companyId);

    CommunityVO getCommunityById(Long id, Long companyId);

    void createCommunity(CommunityDTO dto, Long companyId, String operator);

    void updateCommunity(Long id, CommunityDTO dto, Long companyId, String operator);

    void deleteCommunity(Long id, Long companyId);
}