package com.smart.property.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 车位实体
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@TableName("finance_parking_space")
public class ParkingSpace implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long companyId;

    private Long communityId;

    private String parkingNo;

    private Integer parkingType;

    private BigDecimal parkingArea;

    private Long ownerId;

    private Long tenantId;

    private Integer status;

    private BigDecimal salePrice;

    private LocalDate saleDate;

    private BigDecimal rentPrice;

    private LocalDate rentStartDate;

    private LocalDate rentEndDate;

    private String remark;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
