package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 业委会会议 VO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class CommitteeMeetingVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private String meetingTitle;
    private LocalDate meetingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String meetingContent;
    private String meetingSummary;
    private String attendees;
    private String images;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}