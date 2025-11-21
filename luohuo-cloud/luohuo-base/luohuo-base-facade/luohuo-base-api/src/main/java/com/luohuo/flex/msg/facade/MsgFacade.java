package com.luohuo.flex.msg.facade;


import com.luohuo.flex.msg.vo.update.ExtendMsgSendVO;

/**
 * 消息接口
 *
 * @author 乾乾
 * @since 2024年09月20日10:37:50
 *
 */
public interface MsgFacade {

    /**
     * 根据模板发送消息
     *
     * @param data 发送内容
     * @return
     */
    Boolean sendByTemplate(ExtendMsgSendVO data);
}
