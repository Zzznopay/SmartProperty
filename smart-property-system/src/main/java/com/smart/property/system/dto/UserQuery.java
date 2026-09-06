package com.smart.property.system.dto;

import com.smart.property.common.core.domain.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 用户查询条件
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户名(模糊查询) */
    private String username;

    /** 状态 */
    private Integer status;

    /** 部门ID */
    private Long deptId;
}
