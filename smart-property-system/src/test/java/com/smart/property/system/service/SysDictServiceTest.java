package com.smart.property.system.service;

import com.smart.property.system.vo.SysDictTypeVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 字典服务测试
 *
 * @author zzz
 * @since 2026-07-25
 */
@SpringBootTest
@ActiveProfiles("test")
class SysDictServiceTest {

    @Autowired
    private SysDictService dictService;

    @Test
    void testGetDictTypeList() {
        List<SysDictTypeVO> list = dictService.getDictTypeList();

        assertNotNull(list);
        // 初始数据中没有字典类型，所以列表为空
    }
}
