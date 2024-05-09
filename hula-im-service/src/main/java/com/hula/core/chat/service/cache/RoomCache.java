package com.hula.core.chat.service.cache;

import com.hula.common.constant.RedisKey;
import com.hula.common.service.cache.AbstractRedisStringCache;
import com.hula.core.chat.dao.RoomDao;
import com.hula.core.chat.dao.RoomFriendDao;
import com.hula.core.chat.domain.entity.Room;
import com.hula.core.user.dao.UserDao;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 房间基本信息的缓存
 * @author nyh
 */
@Component
public class RoomCache extends AbstractRedisStringCache<Long, Room> {
    @Resource
    private UserDao userDao;
    @Resource
    private RoomDao roomDao;
    @Resource
    private RoomFriendDao roomFriendDao;

    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_INFO_STRING, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, Room> load(List<Long> roomIds) {
        List<Room> rooms = roomDao.listByIds(roomIds);
        return rooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }
}
