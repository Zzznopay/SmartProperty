package com.smart.property.operation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物品出入 DTO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class GoodsRecordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    /** 1 物品带入 2 物品带出 */
    private Integer recordType;

    @NotBlank(message = "物品名称不能为空")
    private String goodsName;

    private String goodsDesc;

    private Integer quantity;

    private String ownerName;

    private Long roomId;

    private String operatorName;

    private String operatorPhone;

    private LocalDateTime operateTime;

    private Long guardId;

    private String guardName;

    private Integer status;

    private String remark;
}