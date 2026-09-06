package com.smart.property.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.domain.SysLoginLog;
import com.smart.property.system.vo.SysLoginLogVO;

/**
 * 登录日志服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface SysLoginLogService extends IService<SysLoginLog> {

    PageResult<SysLoginLogVO> getLoginLogPage(PageQuery query);

    void cleanAll();

    void removeLog(Long id);
}