package com.smart.property.system.vo;

import com.smart.property.system.domain.FileFolder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件夹VO
 *
 * @author zzz
 * @since 2026-07-27
 */
@Data
public class FileFolderVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long parentId;
    private String folderName;
    private String folderPath;
    private Integer isShared;

    public static FileFolderVO from(FileFolder f) {
        if (f == null) {
            return null;
        }
        FileFolderVO v = new FileFolderVO();
        v.setId(f.getId());
        v.setParentId(f.getParentId());
        v.setFolderName(f.getFolderName());
        v.setFolderPath(f.getFolderPath());
        v.setIsShared(f.getIsShared());
        return v;
    }
}
