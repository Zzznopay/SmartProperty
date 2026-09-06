package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 来访登记 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class VisitRecordVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private String visitorName;
    private String visitorPhone;
    private String visitReason;
    private String visitTarget;
    private Long roomId;
    private String roomNo;
    private LocalDateTime visitTime;
    private LocalDateTime leaveTime;
    private Integer visitorCount;
    private String plateNo;
    private Long guardId;
    private String guardName;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}