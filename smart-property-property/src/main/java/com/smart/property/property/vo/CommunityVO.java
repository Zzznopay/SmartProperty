package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 小区 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class CommunityVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private String communityName;
    private String communityCode;
    private String address;
    private BigDecimal area;
    private Integer buildingCount;
    private Integer roomCount;
    private BigDecimal propertyFee;
    private String contactName;
    private String contactPhone;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}