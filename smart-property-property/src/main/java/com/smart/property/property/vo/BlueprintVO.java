package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图纸 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class BlueprintVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private Long buildingId;
    private String buildingName;
    private String blueprintName;
    private Integer blueprintType;
    private String fileUrl;
    private Long fileSize;
    private String uploadUser;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}