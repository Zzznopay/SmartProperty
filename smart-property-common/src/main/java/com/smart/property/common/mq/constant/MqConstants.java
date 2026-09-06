package com.smart.property.common.mq.constant;

/**
 * RabbitMQ 拓扑常量
 *
 * @author zzz
 * @since 2026-07-25
 */
public final class MqConstants {

    private MqConstants() {
    }

    // ---------------- 服务工单事件 ----------------

    /** 工单事件交换机 */
    public static final String ORDER_EXCHANGE = "property.order.exchange";

    /** 工单创建队列 */
    public static final String ORDER_CREATED_QUEUE = "property.order.created.queue";

    /** 工单创建路由键 */
    public static final String ORDER_CREATED_RK = "property.order.created";

    // ---------------- 缴费事件 ----------------

    /** 缴费事件交换机 */
    public static final String PAYMENT_EXCHANGE = "property.payment.exchange";

    /** 缴费完成队列 */
    public static final String PAYMENT_COMPLETED_QUEUE = "property.payment.completed.queue";

    /** 缴费完成路由键 */
    public static final String PAYMENT_COMPLETED_RK = "property.payment.completed";
}
