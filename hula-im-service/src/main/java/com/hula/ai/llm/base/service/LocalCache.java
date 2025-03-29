package com.hula.ai.llm.base.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;

/**
 * 缓存
 *
 * @author: 云裂痕
 * @date: 2023/9/7
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class LocalCache {

    /**
     * 缓存时长
     */
    public static final long TIMEOUT = 5 * DateUnit.MINUTE.getMillis();


    /**
     * 清理间隔
     */
    private static final long CLEAN_TIMEOUT = 5 * DateUnit.MINUTE.getMillis();

    /**
     * 缓存对象
     */
    public static final TimedCache<Long, Object> CACHE = CacheUtil.newTimedCache(TIMEOUT);

    static {
        //启动定时任务
        CACHE.schedulePrune(CLEAN_TIMEOUT);
    }

}
