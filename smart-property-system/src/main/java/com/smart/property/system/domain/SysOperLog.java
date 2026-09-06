package com.smart.property.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String module;

    private Integer businessType;

    private String method;

    private String requestMethod;

    private String operName;

    private String operUrl;

    private String operIp;

    private String operParam;

    private String jsonResult;

    private Integer status;

    private String errorMsg;

    private Long costTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operTime;
}
