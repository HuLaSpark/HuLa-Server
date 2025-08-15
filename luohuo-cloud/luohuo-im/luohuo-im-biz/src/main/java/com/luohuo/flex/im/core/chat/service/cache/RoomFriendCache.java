package com.luohuo.flex.im.core.chat.service.cache;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import com.luohuo.flex.im.common.constant.RedisKey;
import com.luohuo.flex.im.common.service.cache.AbstractRedisStringCache;
import com.luohuo.flex.im.core.chat.dao.RoomFriendDao;
import com.luohuo.flex.im.domain.entity.RoomFriend;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 群组基本信息的缓存
 * @author nyh
 */
@Component
public class RoomFriendCache extends AbstractRedisStringCache<Long, RoomFriend> {
    @Resource
    private RoomFriendDao roomFriendDao;

    @Override
    protected String getKey(Long groupId) {
        return RedisKey.getKey(RedisKey.ROOM_FRIEND_FORMAT, groupId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, RoomFriend> load(List<Long> roomIds) {
        List<RoomFriend> roomGroups = roomFriendDao.listByRoomIds(roomIds);
        return roomGroups.stream().collect(Collectors.toMap(RoomFriend::getRoomId, Function.identity()));
    }
}
