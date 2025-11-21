package com.luohuo.flex.msg.manager.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.luohuo.basic.base.manager.impl.SuperManagerImpl;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.flex.msg.entity.ExtendMsgRecipient;
import com.luohuo.flex.msg.manager.ExtendMsgRecipientManager;
import com.luohuo.flex.msg.mapper.ExtendMsgRecipientMapper;

import java.util.List;

/**
 * <p>
 * 通用业务实现类
 * 消息接收人
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-10 11:41:17
 * @create [2022-07-10 11:41:17] [zuihou] [代码生成器生成]
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ExtendMsgRecipientManagerImpl extends SuperManagerImpl<ExtendMsgRecipientMapper, ExtendMsgRecipient> implements ExtendMsgRecipientManager {
    @Override
    public List<ExtendMsgRecipient> listByMsgId(Long id) {
        return list(Wraps.<ExtendMsgRecipient>lbQ().eq(ExtendMsgRecipient::getMsgId, id));
    }
}


