package com.smart.property.system.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.vo.SysOperLogVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 操作日志服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SysOperLogServiceTest {

    @Autowired
    private SysOperLogService operLogService;

    @Test
    void testGetOperLogPage() {
        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<SysOperLogVO> result = operLogService.getOperLogPage(query);

        assertNotNull(result);
    }
}
