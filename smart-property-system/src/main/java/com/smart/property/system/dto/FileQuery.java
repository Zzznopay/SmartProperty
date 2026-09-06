package com.smart.property.system.dto;

import com.smart.property.common.core.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件查询条件
 *
 * @author zzz
 * @since 2026-07-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileQuery extends PageQuery {

    /** 业务类型 */
    private String businessType;

    /** 业务ID */
    private Long businessId;

    /** 原始文件名（模糊） */
    private String originalName;
}
