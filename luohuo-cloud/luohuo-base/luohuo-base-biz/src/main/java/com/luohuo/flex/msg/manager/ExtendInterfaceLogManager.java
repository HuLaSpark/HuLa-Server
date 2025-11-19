package com.luohuo.flex.msg.manager;

import com.luohuo.basic.base.manager.SuperManager;
import com.luohuo.flex.msg.entity.ExtendInterfaceLog;

/**
 * <p>
 * 通用业务接口
 * 接口执行日志
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-09 23:58:59
 * @create [2022-07-09 23:58:59] [zuihou] [代码生成器生成]
 */
public interface ExtendInterfaceLogManager extends SuperManager<ExtendInterfaceLog> {

    /***
     * 根据接口ID查询接口执行日志
     * @param interfaceId
     * @return
     */
    ExtendInterfaceLog getByInterfaceId(Long interfaceId);

    /**
     * 递增成功次数
     *
     * @param id 日志ID
     * @return
     */
    void incrSuccessCount(Long id);

    /**
     * 递增失败次数
     *
     * @param id 日志ID
     * @return
     */
    void incrFailCount(Long id);
}


