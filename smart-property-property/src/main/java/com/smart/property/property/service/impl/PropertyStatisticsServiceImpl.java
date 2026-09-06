package com.smart.property.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.smart.property.property.domain.Ledger;
import com.smart.property.property.domain.LeaseContract;
import com.smart.property.property.domain.Owner;
import com.smart.property.property.domain.Payment;
import com.smart.property.property.mapper.BuildingMapper;
import com.smart.property.property.mapper.CommunityMapper;
import com.smart.property.property.mapper.LedgerMapper;
import com.smart.property.property.mapper.LeaseContractMapper;
import com.smart.property.property.mapper.OwnerMapper;
import com.smart.property.property.mapper.ParkingSpaceMapper;
import com.smart.property.property.mapper.PaymentMapper;
import com.smart.property.property.mapper.RoomMapper;
import com.smart.property.property.service.PropertyStatisticsService;
import com.smart.property.property.vo.PropertyOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 房产财务统计服务实现
 * <p>
 * 聚合查询均通过 MyBatis-Plus {@link QueryWrapper} 完成，不引入自定义 SQL/XML。
 *
 * @author zzz
 * @since 2026-07-29
 */
@Service
@RequiredArgsConstructor
public class PropertyStatisticsServiceImpl implements PropertyStatisticsService {

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final CommunityMapper communityMapper;
    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final OwnerMapper ownerMapper;
    private final ParkingSpaceMapper parkingSpaceMapper;
    private final LeaseContractMapper leaseContractMapper;
    private final PaymentMapper paymentMapper;
    private final LedgerMapper ledgerMapper;

    @Override
    public PropertyOverviewVO getOverview(Long companyId) {
        PropertyOverviewVO vo = new PropertyOverviewVO();
        vo.setCommunityCount(communityMapper.selectCount(new QueryWrapper<com.smart.property.property.domain.Community>()
                .eq("company_id", companyId)));
        vo.setBuildingCount(buildingMapper.selectCount(new QueryWrapper<com.smart.property.property.domain.Building>()
                .eq("company_id", companyId)));
        vo.setRoomCount(roomMapper.selectCount(new QueryWrapper<com.smart.property.property.domain.Room>()
                .eq("company_id", companyId)));
        vo.setOwnerCount(ownerMapper.selectCount(new QueryWrapper<Owner>()
                .eq("company_id", companyId)));
        vo.setParkingSpaceCount(parkingSpaceMapper.selectCount(new QueryWrapper<com.smart.property.property.domain.ParkingSpace>()
                .eq("company_id", companyId)));
        // 生效租赁合同：status=2
        vo.setActiveLeaseCount(leaseContractMapper.selectCount(new QueryWrapper<LeaseContract>()
                .eq("company_id", companyId).eq("status", 2)));
        // 本月收费金额：status=1(正常) 且 pay_time 在本月
        LocalDate today = LocalDate.now();
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime nextMonthStart = monthStart.plusMonths(1);
        vo.setMonthPaymentAmount(sumAmount(companyId, monthStart, nextMonthStart));
        // 待缴费账单：status in (1未收,2部分收)
        vo.setPendingLedgerCount(ledgerMapper.selectCount(new QueryWrapper<Ledger>()
                .eq("company_id", companyId).in("status", 1, 2)));
        return vo;
    }

    @Override
    public List<Map<String, Object>> getPaymentTrend(Long companyId, int months) {
        int n = Math.max(1, months);
        LocalDate today = LocalDate.now();
        List<String> labels = new ArrayList<>();
        for (int i = n - 1; i >= 0; i--) {
            labels.add(today.minusMonths(i).format(MONTH_FMT));
        }
        LocalDateTime startDate = today.minusMonths(n - 1).withDayOfMonth(1).atStartOfDay();

        QueryWrapper<Payment> wrapper = new QueryWrapper<Payment>()
                .select("DATE_FORMAT(pay_time,'%Y-%m') as month", "IFNULL(SUM(actual_amount),0) as amount")
                .eq("company_id", companyId)
                .eq("status", 1)
                .ge("pay_time", startDate)
                .groupBy("month")
                .orderByAsc("month");
        List<Map<String, Object>> rows = paymentMapper.selectMaps(wrapper);

        Map<String, Object> indexed = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            indexed.put(String.valueOf(row.get("month")), row.get("amount"));
        }
        // 补齐无数据月份为 0，保证图表横轴连续
        List<Map<String, Object>> result = new ArrayList<>();
        for (String m : labels) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", m);
            Object val = indexed.get(m);
            item.put("amount", val == null ? BigDecimal.ZERO : toBigDecimal(val));
            result.add(item);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getFeeStatusDistribution(Long companyId) {
        QueryWrapper<Ledger> wrapper = new QueryWrapper<Ledger>()
                .select("status", "COUNT(*) as count")
                .eq("company_id", companyId)
                .groupBy("status");
        return ledgerMapper.selectMaps(wrapper);
    }

    private BigDecimal sumAmount(Long companyId, LocalDateTime from, LocalDateTime to) {
        QueryWrapper<Payment> wrapper = new QueryWrapper<Payment>()
                .select("IFNULL(SUM(actual_amount),0) as total")
                .eq("company_id", companyId)
                .eq("status", 1)
                .ge("pay_time", from)
                .lt("pay_time", to);
        List<Map<String, Object>> rows = paymentMapper.selectMaps(wrapper);
        if (rows == null || rows.isEmpty() || rows.get(0).get("total") == null) {
            return BigDecimal.ZERO;
        }
        return toBigDecimal(rows.get(0).get("total"));
    }

    private static BigDecimal toBigDecimal(Object val) {
        if (val == null) {
            return BigDecimal.ZERO;
        }
        if (val instanceof BigDecimal bd) {
            return bd;
        }
        return new BigDecimal(val.toString());
    }
}
