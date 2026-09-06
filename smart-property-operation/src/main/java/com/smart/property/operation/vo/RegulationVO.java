package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 规章制度 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class RegulationVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private String title;
    private String content;
    private String category;
    private String fileUrl;
    private String fileName;
    private Integer isPublish;
    private LocalDateTime publishTime;
    private Integer viewCount;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}