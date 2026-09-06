package com.smart.property.system.convert;

import com.smart.property.system.domain.SysMenu;
import com.smart.property.system.dto.SysMenuDTO;
import com.smart.property.system.vo.MenuTreeNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * SysMenu Entity ↔ DTO/VO 转换器。
 *
 * <p>Controller 现使用 MenuTreeNode 作为出参 VO（树形结构），本 Converter 同时支持
 * 单条 DTO→Entity 与列表 Entity→MenuTreeNode 的扁平映射；children 由 Controller 手工构树。</p>
 *
 * @author zzz
 * @since 2026-07-29
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SysMenuConverter {

    @Mapping(target = "children", ignore = true)
    MenuTreeNode toTreeNode(SysMenu entity);

    List<MenuTreeNode> toTreeNodeList(List<SysMenu> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "children", ignore = true)
    SysMenu toEntity(SysMenuDTO dto);
}