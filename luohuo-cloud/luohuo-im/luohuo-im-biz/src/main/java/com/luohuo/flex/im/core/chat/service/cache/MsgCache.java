package com.luohuo.flex.im.core.chat.service.cache;

import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.core.user.dao.BlackDao;
import com.luohuo.flex.im.core.user.dao.RoleDao;
import com.luohuo.flex.im.core.user.dao.UserDao;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 消息相关缓存
 * @author nyh
 */
@Component
public class MsgCache {

    @Resource
    private UserDao userDao;
    @Resource
    private BlackDao blackDao;
    @Resource
    private RoleDao roleDao;
    @Resource
    private MessageDao messageDao;

    @Cacheable(cacheNames = "luohuo:msg", key = "#msgId", unless = "#result == null")
    public Message getMsg(Long msgId) {
        return messageDao.getById(msgId);
    }

    @CacheEvict(cacheNames = "luohuo:msg", key = "#msgId")
    public Message evictMsg(Long msgId) {
        return null;
    }
}
