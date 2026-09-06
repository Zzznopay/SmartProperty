package com.smart.property.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 消防设施 DTO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class FireFacilityDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    private Long buildingId;

    @NotBlank(message = "设施名称不能为空")
    private String facilityName;

    /** 1 灭火器 2 烟感 3 喷淋 4 应急灯 5 消火栓 */
    private Integer facilityType;

    private String facilityNo;

    private String location;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate installDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastCheckDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextCheckDate;

    private String remark;
}