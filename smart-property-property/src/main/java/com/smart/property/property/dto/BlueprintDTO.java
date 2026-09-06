package com.smart.property.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图纸 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class BlueprintDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "小区ID不能为空")
    private Long communityId;

    private Long buildingId;

    @NotBlank(message = "图纸名称不能为空")
    private String blueprintName;

    /** 1 建筑 2 结构 3 水电 4 消防 */
    private Integer blueprintType;

    @NotBlank(message = "文件URL不能为空")
    private String fileUrl;

    private Long fileSize;

    private String uploadUser;

    private String remark;
}