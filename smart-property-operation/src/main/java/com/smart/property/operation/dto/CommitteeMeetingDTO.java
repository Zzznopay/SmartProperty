package com.smart.property.operation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 业委会会议 DTO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class CommitteeMeetingDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @NotBlank(message = "会议标题不能为空")
    private String meetingTitle;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate meetingDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private String location;

    private String meetingContent;

    private String meetingSummary;

    private String attendees;

    private String images;

    private Integer status;

    private String remark;
}