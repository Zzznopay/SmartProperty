package com.smart.property.system.service;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.vo.SysLoginLogVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 登录日志服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SysLoginLogServiceTest {

    @Autowired
    private SysLoginLogService loginLogService;

    @Test
    void testGetLoginLogPage() {
        PageQuery query = new PageQuery();
        query.setPageNum(1);
        query.setPageSize(10);

        PageResult<SysLoginLogVO> result = loginLogService.getLoginLogPage(query);

        assertNotNull(result);
    }
}
