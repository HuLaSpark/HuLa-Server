package com.luohuo.flex.im.core.chat.service.cache;

import com.luohuo.flex.im.common.service.cache.AbstractRedisStringCache;
import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.domain.entity.Message;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 升级消息相关缓存
 * @author nyh
 */
@Component
public class MsgPlusCache extends AbstractRedisStringCache<Long, Message> {
    @Resource
    private MessageDao messageDao;

    @Override
    protected String getKey(Long messageId) {
        return "msg" + messageId;
    }

    @Override
    protected Long getExpireSeconds() {
        return 3 * 60L;
    }

    @Override
    protected Map<Long, Message> load(List<Long> messageIds) {
        return messageDao.listByIds(messageIds)
                .stream().collect(Collectors.toMap(Message::getId, Function.identity()));
    }

    @Override
    public void delete(Long messageId) {
        super.delete(messageId);
    }
}
