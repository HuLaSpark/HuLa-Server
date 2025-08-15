package com.luohuo.basic.log.event;


import org.springframework.context.ApplicationEvent;
import com.luohuo.basic.model.log.OptLogDTO;

/**
 * 系统日志事件
 *
 * @author zuihou
 * @date 2019-07-01 15:13
 */
public class SysLogEvent extends ApplicationEvent {

    public SysLogEvent(OptLogDTO source) {
        super(source);
    }
}
