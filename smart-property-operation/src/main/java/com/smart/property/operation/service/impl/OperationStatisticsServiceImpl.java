package com.smart.property.operation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.smart.property.operation.domain.CleanCheck;
import com.smart.property.operation.domain.CommunityActivity;
import com.smart.property.operation.domain.FirePatrol;
import com.smart.property.operation.domain.GreeneryCheck;
import com.smart.property.operation.domain.Notice;
import com.smart.property.operation.domain.ServiceOrder;
import com.smart.property.operation.domain.VehicleRecord;
import com.smart.property.operation.domain.VisitRecord;
import com.smart.property.operation.mapper.CleanCheckMapper;
import com.smart.property.operation.mapper.CommunityActivityMapper;
import com.smart.property.operation.mapper.FirePatrolMapper;
import com.smart.property.operation.mapper.GreeneryCheckMapper;
import com.smart.property.operation.mapper.NoticeMapper;
import com.smart.property.operation.mapper.ServiceOrderMapper;
import com.smart.property.operation.mapper.VehicleRecordMapper;
import com.smart.property.operation.mapper.VisitRecordMapper;
import com.smart.property.operation.service.OperationStatisticsService;
import com.smart.property.operation.vo.OperationOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营管理统计服务实现
 * <p>
 * 聚合查询均通过 MyBatis-Plus {@link QueryWrapper} 完成，不引入自定义 SQL/XML。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Service
@RequiredArgsConstructor
public class OperationStatisticsServiceImpl implements OperationStatisticsService {

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final ServiceOrderMapper serviceOrderMapper;
    private final VisitRecordMapper visitRecordMapper;
    private final VehicleRecordMapper vehicleRecordMapper;
    private final NoticeMapper noticeMapper;
    private final CommunityActivityMapper communityActivityMapper;
    private final CleanCheckMapper cleanCheckMapper;
    private final FirePatrolMapper firePatrolMapper;
    private final GreeneryCheckMapper greeneryCheckMapper;

    @Override
    public OperationOverviewVO getOverview(Long companyId) {
        OperationOverviewVO vo = new OperationOverviewVO();
        vo.setServiceOrderCount(serviceOrderMapper.selectCount(new QueryWrapper<ServiceOrder>()
                .eq("company_id", companyId)));
        // 待处理工单：status in (1待分配,2处理中,3待回访)
        vo.setPendingServiceOrderCount(serviceOrderMapper.selectCount(new QueryWrapper<ServiceOrder>()
                .eq("company_id", companyId).in("status", 1, 2, 3)));

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime startOfNextDay = startOfDay.plusDays(1);
        vo.setTodayVisitCount(visitRecordMapper.selectCount(new QueryWrapper<VisitRecord>()
                .eq("company_id", companyId).ge("visit_time", startOfDay).lt("visit_time", startOfNextDay)));
        vo.setTodayVehicleCount(vehicleRecordMapper.selectCount(new QueryWrapper<VehicleRecord>()
                .eq("company_id", companyId).ge("record_time", startOfDay).lt("record_time", startOfNextDay)));

        // 已发布公告：status=2
        vo.setNoticeCount(noticeMapper.selectCount(new QueryWrapper<Notice>()
                .eq("company_id", companyId).eq("status", 2)));
        vo.setCommunityActivityCount(communityActivityMapper.selectCount(new QueryWrapper<CommunityActivity>()
                .eq("company_id", companyId)));
        vo.setCleanCheckCount(cleanCheckMapper.selectCount(new QueryWrapper<CleanCheck>()
                .eq("company_id", companyId)));
        vo.setFirePatrolCount(firePatrolMapper.selectCount(new QueryWrapper<FirePatrol>()
                .eq("company_id", companyId)));
        vo.setGreeneryCheckCount(greeneryCheckMapper.selectCount(new QueryWrapper<GreeneryCheck>()
                .eq("company_id", companyId)));
        return vo;
    }

    @Override
    public List<Map<String, Object>> getServiceOrderStatusDistribution(Long companyId) {
        QueryWrapper<ServiceOrder> wrapper = new QueryWrapper<ServiceOrder>()
                .select("status", "COUNT(*) as count")
                .eq("company_id", companyId)
                .groupBy("status");
        return serviceOrderMapper.selectMaps(wrapper);
    }

    @Override
    public List<Map<String, Object>> getServiceOrderTrend(Long companyId, int months) {
        int n = Math.max(1, months);
        LocalDate today = LocalDate.now();
        List<String> labels = new ArrayList<>();
        for (int i = n - 1; i >= 0; i--) {
            labels.add(today.minusMonths(i).format(MONTH_FMT));
        }
        LocalDateTime startDate = today.minusMonths(n - 1).withDayOfMonth(1).atStartOfDay();

        QueryWrapper<ServiceOrder> wrapper = new QueryWrapper<ServiceOrder>()
                .select("DATE_FORMAT(create_time,'%Y-%m') as month", "COUNT(*) as count")
                .eq("company_id", companyId)
                .ge("create_time", startDate)
                .groupBy("month")
                .orderByAsc("month");
        List<Map<String, Object>> rows = serviceOrderMapper.selectMaps(wrapper);

        Map<String, Object> indexed = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            indexed.put(String.valueOf(row.get("month")), row.get("count"));
        }
        // 补齐无数据月份为 0，保证图表横轴连续
        List<Map<String, Object>> result = new ArrayList<>();
        for (String m : labels) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", m);
            Object val = indexed.get(m);
            item.put("count", val == null ? 0L : toLong(val));
            result.add(item);
        }
        return result;
    }

    private static long toLong(Object val) {
        if (val == null) {
            return 0L;
        }
        if (val instanceof Number num) {
            return num.longValue();
        }
        return Long.parseLong(val.toString());
    }
}
