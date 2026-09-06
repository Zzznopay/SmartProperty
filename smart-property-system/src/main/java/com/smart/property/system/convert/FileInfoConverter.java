package com.smart.property.system.convert;

import com.smart.property.system.domain.FileInfo;
import com.smart.property.system.vo.FileInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * FileInfo Entity -> VO 转换器（替代原 FileInfoVO#from 静态方法）。
 *
 * @author zzz
 * @since 2026-07-30
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileInfoConverter {

    FileInfoVO toVO(FileInfo entity);

    List<FileInfoVO> toVOList(List<FileInfo> entities);
}