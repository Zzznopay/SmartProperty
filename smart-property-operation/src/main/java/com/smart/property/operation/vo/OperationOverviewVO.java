package com.smart.property.operation.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 运营管理统计概览 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class OperationOverviewVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 工单总数 */
    private Long serviceOrderCount;

    /** 待处理工单数(待分配/处理中/待回访) */
    private Long pendingServiceOrderCount;

    /** 今日来访登记数 */
    private Long todayVisitCount;

    /** 今日车辆进出数 */
    private Long todayVehicleCount;

    /** 已发布公告数 */
    private Long noticeCount;

    /** 社区活动数 */
    private Long communityActivityCount;

    /** 清洁检查数 */
    private Long cleanCheckCount;

    /** 消防巡查数 */
    private Long firePatrolCount;

    /** 绿化检查数 */
    private Long greeneryCheckCount;
}
