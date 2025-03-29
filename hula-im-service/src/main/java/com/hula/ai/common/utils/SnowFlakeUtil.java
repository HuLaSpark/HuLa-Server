package com.hula.ai.common.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import com.hula.ai.common.enums.IntEnum;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;


/**
 * 获取雪花算法Id
 *
 * @author: 云裂痕
 * @date: 2020/12/16
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Slf4j
public class SnowFlakeUtil {

    private static long workerId = 0;
    private static long dataCenterId = 1;
    private static int snowflakeLength = 19;
    private static Snowflake snowflake = IdUtil.createSnowflake(workerId, dataCenterId);

    @PostConstruct
    public void init() {
        try {
            workerId = NetUtil.ipv4ToLong(NetUtil.getLocalhostStr());
            log.info("当前机器的workId: {}", workerId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("当前机器的workId获取失败", e);
            workerId = NetUtil.getLocalhostStr().hashCode();
        }
    }

    public static synchronized long snowflakeId() {
        return snowflake.nextId();
    }

    public static synchronized long snowflakeId(int length) {
        long snowflakeId = snowflakeId();
        if (length <= IntEnum.ZERO.getValue()) {
            return snowflakeId;
        }
        return Long.valueOf(String.valueOf(snowflakeId).substring(snowflakeLength - length));
    }

    public static synchronized long snowflakeId(long workerId, long dataCenterId) {
        Snowflake snowflake = IdUtil.createSnowflake(workerId, dataCenterId);
        return snowflake.nextId();
    }

    public static synchronized long snowflakeId(long workerId, long dataCenterId, int length) {
        long snowflakeId = snowflakeId(workerId, dataCenterId);
        if (length <= IntEnum.ZERO.getValue()) {
            return snowflakeId;
        }
        return Long.valueOf(String.valueOf(snowflakeId).substring(snowflakeLength - length));
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Stream.iterate(0, x -> x + 1).limit(20).
                forEach(x -> {
                    executorService.submit(() -> {
                        long id = snowflakeId(13);
                        System.out.println(id);
                    });
                });
    }


}
