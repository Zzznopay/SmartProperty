package com.smart.property.operation.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.operation.domain.CommunityActivity;
import com.smart.property.operation.vo.CommunityActivityVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 社区活动服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@Transactional
class CommunityActivityServiceTest {

    @Autowired
    private CommunityActivityService communityActivityService;

    @Test
    void testSaveActivity() {
        CommunityActivity activity = new CommunityActivity();
        activity.setCompanyId(1L);
        activity.setCommunityId(1L);
        activity.setActivityName("中秋晚会");
        activity.setActivityType(3);
        activity.setActivityDate(LocalDate.now().plusDays(30));
        activity.setStartTime(LocalTime.of(19, 0));
        activity.setEndTime(LocalTime.of(21, 0));
        activity.setLocation("小区广场");
        activity.setContent("中秋联欢晚会");
        activity.setBudget(new BigDecimal("5000"));
        activity.setStatus(1);
        activity.setCreateBy("test");
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());

        boolean saved = communityActivityService.save(activity);

        assertTrue(saved);
        assertNotNull(activity.getId());
    }

    @Test
    void testGetActivityPage() {
        CommunityActivity activity = new CommunityActivity();
        activity.setCompanyId(1L);
        activity.setCommunityId(1L);
        activity.setActivityName("分页测试活动");
        activity.setActivityType(1);
        activity.setActivityDate(LocalDate.now());
        activity.setStartTime(LocalTime.of(10, 0));
        activity.setEndTime(LocalTime.of(12, 0));
        activity.setLocation("活动中心");
        activity.setContent("测试内容");
        activity.setStatus(1);
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        communityActivityService.save(activity);

        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<CommunityActivityVO> result = communityActivityService.getActivityPage(query, 1L, null, null);

        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    void testCompleteActivity() {
        CommunityActivity activity = new CommunityActivity();
        activity.setCompanyId(1L);
        activity.setCommunityId(1L);
        activity.setActivityName("完成测试活动");
        activity.setActivityType(2);
        activity.setActivityDate(LocalDate.now());
        activity.setStartTime(LocalTime.of(14, 0));
        activity.setEndTime(LocalTime.of(16, 0));
        activity.setLocation("会议室");
        activity.setContent("测试内容");
        activity.setStatus(1);
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        communityActivityService.save(activity);

        communityActivityService.completeActivity(activity.getId(), "test");

        CommunityActivity completed = communityActivityService.getById(activity.getId());
        assertEquals(3, completed.getStatus());
    }
}
