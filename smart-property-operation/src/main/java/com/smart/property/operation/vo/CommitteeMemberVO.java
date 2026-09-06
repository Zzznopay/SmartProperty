package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 业委会成员 VO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class CommitteeMemberVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private String memberName;
    private String position;
    private String phone;
    private Long roomId;
    private String roomNo;
    private LocalDate termStart;
    private LocalDate termEnd;
    private String photo;
    private String introduction;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}