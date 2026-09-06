package com.smart.property.property.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.property.property.domain.Ledger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 物业费台帐Mapper接口
 *
 * @author zzz
 * @since 2026-07-25
 */
@Mapper
public interface LedgerMapper extends BaseMapper<Ledger> {

    /**
     * 原子累加已收金额（并发安全）
     *
     * <p>单条 UPDATE 原子性保证：MySQL InnoDB REPEATABLE READ 隔离级别下，
     * 条件中的 {@code paid_amount + #{delta} &lt;= amount} 在加行锁后比对，
     * 避免两个并发请求读到旧值后互相覆盖。</p>
     *
     * @param ledgerId 台账 ID
     * @param delta    累加金额（正=收款，负=退款）
     * @param operator 操作人
     * @return 受影响行数；0 = 累加后超 amount / 台账不存在
     */
    @Update("UPDATE finance_ledger SET paid_amount = paid_amount + #{delta}, " +
            "update_by = #{operator}, update_time = NOW() " +
            "WHERE id = #{ledgerId} AND is_deleted = 0 " +
            "AND paid_amount + #{delta} >= 0 " +
            "AND paid_amount + #{delta} <= amount")
    int accumulatePaidAmount(@Param("ledgerId") Long ledgerId,
                             @Param("delta") java.math.BigDecimal delta,
                             @Param("operator") String operator);

    /**
     * 同步台账状态（收款后基于新 paid_amount 计算 1/2/3）
     *
     * @param ledgerId 台账 ID
     * @param status   新状态（1=未收 2=部分收 3=已收）
     * @param payTime  支付时间
     * @param operator 操作人
     */
    @Update("UPDATE finance_ledger SET status = #{status}, pay_time = #{payTime}, " +
            "update_by = #{operator}, update_time = NOW() " +
            "WHERE id = #{ledgerId} AND is_deleted = 0")
    int updateLedgerStatus(@Param("ledgerId") Long ledgerId,
                           @Param("status") Integer status,
                           @Param("payTime") java.time.LocalDateTime payTime,
                           @Param("operator") String operator);
}
