package com.luohuo.basic.interfaces.echo;

import java.util.Map;

/**
 * 注入VO 父类
 *
 * @author zuihou
 * @date 2021/3/22 2:22 下午
 */
public interface EchoVO {

    /**
     * 回显值 集合
     *
     * @return 回显值 集合
     */
    Map<String, Object> getEchoMap();
}
