package com.smart.property.system.vo;

import com.smart.property.system.domain.FileDisk;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 网盘文件VO
 *
 * @author zzz
 * @since 2026-07-27
 */
@Data
public class FileDiskVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long fileId;
    private Long folderId;
    private String fileName;
    private Integer isShared;
    private String fileUrl;

    public static FileDiskVO from(FileDisk d) {
        if (d == null) {
            return null;
        }
        FileDiskVO v = new FileDiskVO();
        v.setId(d.getId());
        v.setFileId(d.getFileId());
        v.setFolderId(d.getFolderId());
        v.setFileName(d.getFileName());
        v.setIsShared(d.getIsShared());
        return v;
    }
}
