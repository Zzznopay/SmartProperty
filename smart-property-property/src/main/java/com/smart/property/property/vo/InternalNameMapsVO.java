package com.smart.property.property.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 内部批量名称解析 VO（供其他微服务经 Feign 调用）
 *
 * <p>key 为资源 id（Jackson 序列化为字符串，反序列化端同样映射回 Long）。</p>
 *
 * @author zzz
 * @since 2026-09-06
 */
@Data
public class InternalNameMapsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 小区 id → 小区名称 */
    private Map<Long, String> communities = new HashMap<>();

    /** 楼宇 id → 楼宇名称 */
    private Map<Long, String> buildings = new HashMap<>();

    /** 房间 id → 房号 */
    private Map<Long, String> rooms = new HashMap<>();
}
