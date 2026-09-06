package com.smart.property.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.domain.SysOperLog;
import com.smart.property.system.vo.SysOperLogVO;

/**
 * 操作日志服务接口
 *
 * @author zzz
 * @since 2026-07-25
 */
public interface SysOperLogService extends IService<SysOperLog> {

    PageResult<SysOperLogVO> getOperLogPage(PageQuery query);

    void cleanAll();

    void removeLog(Long id);
}