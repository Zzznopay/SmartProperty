package com.smart.property.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.property.system.domain.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单Mapper接口
 *
 * @author zzz
 * @since 2026-07-25
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户ID查询菜单列表
     */
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);
}
