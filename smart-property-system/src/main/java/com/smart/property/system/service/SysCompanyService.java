package com.smart.property.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.domain.SysCompany;
import com.smart.property.system.dto.SysCompanyDTO;
import com.smart.property.system.vo.SysCompanyVO;

import java.util.List;

/**
 * 物业公司服务
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface SysCompanyService extends IService<SysCompany> {
    List<SysCompanyVO> getCompanyTree();
    PageResult<SysCompanyVO> getCompanyPage(int pageNum, int pageSize, String companyName);
    SysCompanyVO getCompanyById(Long id);
    void createCompany(SysCompanyDTO dto, String operator);
    void updateCompany(Long id, SysCompanyDTO dto, String operator);
}