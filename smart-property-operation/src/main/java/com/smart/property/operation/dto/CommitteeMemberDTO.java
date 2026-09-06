package com.smart.property.operation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 业委会成员 DTO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class CommitteeMemberDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @NotBlank(message = "成员姓名不能为空")
    private String memberName;

    /** 主任 / 副主任 / 委员 */
    private String position;

    private String phone;

    private Long roomId;

    private LocalDate termStart;

    private LocalDate termEnd;

    private String photo;

    private String introduction;

    private Integer status;

    private String remark;
}