package com.luohuo.flex.msg.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.service.impl.SuperServiceImpl;

import com.luohuo.flex.msg.entity.ExtendMsgRecipient;
import com.luohuo.flex.msg.manager.ExtendMsgRecipientManager;
import com.luohuo.flex.msg.service.ExtendMsgRecipientService;

import java.util.List;

/**
 * <p>
 * 业务实现类
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
@Transactional(readOnly = true)
public class ExtendMsgRecipientServiceImpl extends SuperServiceImpl<ExtendMsgRecipientManager, Long, ExtendMsgRecipient> implements ExtendMsgRecipientService {
    @Override
    public List<ExtendMsgRecipient> listByMsgId(Long id) {
        return superManager.listByMsgId(id);
    }

}


