package com.smart.property.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 物业公司 VO（含 children 字段，用于返回树形结构）
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class SysCompanyVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long parentId;
    private String companyName;
    private String companyCode;
    private String contactName;
    private String contactPhone;
    private String address;
    private String logo;
    private Integer status;
    private String remark;
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private List<SysCompanyVO> children = new ArrayList<>();
}