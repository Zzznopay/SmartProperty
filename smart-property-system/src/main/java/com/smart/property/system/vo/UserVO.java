package com.smart.property.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户VO
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
public class UserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long deptId;
    private String deptName;
    private String username;
    private String realName;
    private String phoneMask;
    private String email;
    private String avatar;
    private Integer gender;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 角色列表 */
    private List<String> roles;
}
