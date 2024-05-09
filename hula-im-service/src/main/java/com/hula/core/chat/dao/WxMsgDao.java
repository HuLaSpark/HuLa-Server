package com.hula.core.chat.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.core.chat.domain.entity.WxMsg;
import com.hula.core.chat.mapper.WxMsgMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 微信消息表 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class WxMsgDao extends ServiceImpl<WxMsgMapper, WxMsg> {

}
