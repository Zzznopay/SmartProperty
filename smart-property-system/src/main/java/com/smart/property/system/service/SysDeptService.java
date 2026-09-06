package com.smart.property.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.system.domain.SysDept;
import com.smart.property.system.vo.SysDeptVO;

import java.util.List;

/**
 * 部门服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 查询部门树
     */
    List<SysDeptVO> getDeptTree(Long companyId);
}