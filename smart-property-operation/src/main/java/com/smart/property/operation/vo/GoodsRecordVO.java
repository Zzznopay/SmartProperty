package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物品出入 VO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class GoodsRecordVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private Integer recordType;
    private String goodsName;
    private String goodsDesc;
    private Integer quantity;
    private String ownerName;
    private Long roomId;
    private String roomNo;
    private String operatorName;
    private String operatorPhone;
    private LocalDateTime operateTime;
    private Long guardId;
    private String guardName;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}