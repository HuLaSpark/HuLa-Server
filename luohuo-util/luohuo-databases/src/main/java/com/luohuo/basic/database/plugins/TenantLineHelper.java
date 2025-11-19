package com.luohuo.basic.database.plugins;

import com.baomidou.mybatisplus.core.toolkit.StringPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tangyh
 * @version v1.0
 * @date 2022/8/25 1:12 PM
 * @create [2022/8/25 1:12 PM ] [tangyh] [初始创建]
 */
public class TenantLineHelper {
    final static Map<String, Boolean> CACHE = new ConcurrentHashMap<>();

    /**
     * 判断 mapper id 是否启用了 @TenantLine 注解
     *
     * @param id mapper 唯一
     * @author tangyh
     * @date 2022/8/25 1:15 PM
     * @create [2022/8/25 1:15 PM ] [tangyh] [初始创建]
     */
    public static boolean willTenantLine(String id) {
        Boolean cache = CACHE.get(id);
        if (cache == null) {
            cache = CACHE.get(id.substring(0, id.lastIndexOf(StringPool.DOT)));
        }
        if (cache != null) {
            return cache;
        }
        return false;
    }

}
