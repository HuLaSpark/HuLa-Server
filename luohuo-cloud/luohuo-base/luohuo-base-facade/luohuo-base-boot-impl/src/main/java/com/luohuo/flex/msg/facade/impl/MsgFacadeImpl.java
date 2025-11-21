package com.luohuo.flex.msg.facade.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.luohuo.flex.msg.biz.MsgBiz;
import com.luohuo.flex.msg.facade.MsgFacade;
import com.luohuo.flex.msg.vo.update.ExtendMsgSendVO;

/**
 * 消息接口
 *
 * @author 乾乾
 * @since 2024年09月20日10:37:50
 *
 */
@Service
@RequiredArgsConstructor
public class MsgFacadeImpl implements MsgFacade {
    private final MsgBiz msgBiz;

    /**
     * 根据模板发送消息
     *
     * @param data 发送内容
     * @return
     */
    @Override
    public Boolean sendByTemplate(ExtendMsgSendVO data) {
        return msgBiz.sendByTemplate(data, null);
    }
}
