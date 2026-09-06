package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 业主 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class OwnerVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private String ownerCode;
    private String ownerName;
    private Integer gender;
    private String idCardMask;
    private String phoneMask;
    private String email;
    private String wechat;
    private String address;
    private Integer ownerType;
    private String emergencyContact;
    private String emergencyPhone;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}