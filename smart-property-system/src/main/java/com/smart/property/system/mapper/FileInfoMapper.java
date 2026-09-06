package com.smart.property.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.property.system.domain.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 文件Mapper
 *
 * @author zzz
 * @since 2026-07-27
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    /**
     * 秒传查询：按 (company_id, business_type, md5) 找一条 status=1 的记录
     */
    @Select("SELECT * FROM file_info " +
            "WHERE company_id = #{companyId} " +
            "AND business_type = #{businessType} " +
            "AND md5 = #{md5} " +
            "AND status = 1 " +
            "AND is_deleted = 0 " +
            "LIMIT 1")
    FileInfo selectByMd5AndType(@Param("companyId") Long companyId,
                                 @Param("businessType") String businessType,
                                 @Param("md5") String md5);

    /**
     * 累加下载次数
     */
    @Update("UPDATE file_info SET download_count = download_count + 1 WHERE id = #{id} AND is_deleted = 0")
    int incrementDownloadCount(@Param("id") Long id);
}
