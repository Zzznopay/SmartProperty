package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 验房记录 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class CheckRecordVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long roomId;
    private String roomNo;
    private Long ownerId;
    private String ownerName;
    private Integer checkType;
    private LocalDate checkDate;
    private Integer checkResult;
    private String problems;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}