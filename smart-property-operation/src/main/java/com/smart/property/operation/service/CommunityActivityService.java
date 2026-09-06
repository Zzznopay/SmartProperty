package com.smart.property.operation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.CommunityActivity;
import com.smart.property.operation.dto.CommunityActivityDTO;
import com.smart.property.operation.vo.CommunityActivityVO;

/**
 * 社区活动服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface CommunityActivityService extends IService<CommunityActivity> {

    PageResult<CommunityActivityVO> getActivityPage(PageQuery query, Long companyId, Long communityId, Integer activityType);

    void createActivity(CommunityActivityDTO dto, Long companyId, String operator);

    void updateActivity(Long id, CommunityActivityDTO dto, String operator);

    void completeActivity(Long id, String operator);
}