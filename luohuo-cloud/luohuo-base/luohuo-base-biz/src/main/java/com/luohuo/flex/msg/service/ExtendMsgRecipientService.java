package com.luohuo.flex.msg.service;

import com.luohuo.basic.base.service.SuperService;
import com.luohuo.flex.msg.entity.ExtendMsgRecipient;

import java.util.List;


/**
 * <p>
 * 业务接口
 * 消息接收人
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-10 11:41:17
 * @create [2022-07-10 11:41:17] [zuihou] [代码生成器生成]
 */
public interface ExtendMsgRecipientService extends SuperService<Long, ExtendMsgRecipient> {
    /**
     * 根据消息ID查询接收人
     *
     * @param id
     * @return
     */
    List<ExtendMsgRecipient> listByMsgId(Long id);
}


