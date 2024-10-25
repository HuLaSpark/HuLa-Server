package com.hula.core.chat.service.cache;

import com.hula.core.chat.dao.MessageDao;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.user.dao.BlackDao;
import com.hula.core.user.dao.RoleDao;
import com.hula.core.user.dao.UserDao;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
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

//    @Cacheable(cacheNames = "msg", key = "'msg'+#msgId")
    public Message getMsg(Long msgId) {
        return messageDao.getById(msgId);
    }

    @CacheEvict(cacheNames = "msg", key = "'msg'+#msgId")
    public Message evictMsg(Long msgId) {
        return null;
    }
}
