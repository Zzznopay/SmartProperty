package com.smart.property.system.service;

import com.smart.property.system.vo.SysDeptVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 部门服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@ActiveProfiles("test")
class SysDeptServiceTest {

    @Autowired
    private SysDeptService deptService;

    @Test
    void testGetDeptTree() {
        List<SysDeptVO> tree = deptService.getDeptTree(1L);

        assertNotNull(tree);
        assertFalse(tree.isEmpty());

        // 验证树形结构
        SysDeptVO root = tree.get(0);
        assertEquals("总公司", root.getDeptName());
        assertNotNull(root.getChildren());
    }
}
