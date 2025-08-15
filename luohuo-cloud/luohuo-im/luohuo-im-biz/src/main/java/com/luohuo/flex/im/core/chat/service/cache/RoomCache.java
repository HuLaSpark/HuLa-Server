package com.luohuo.flex.im.core.chat.service.cache;

import com.luohuo.flex.im.common.constant.RedisKey;
import com.luohuo.flex.im.common.service.cache.AbstractRedisStringCache;
import com.luohuo.flex.im.core.chat.dao.RoomDao;
import com.luohuo.flex.im.domain.entity.Room;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
    private RoomDao roomDao;

    @Override
    protected String getKey(Long roomId) {
        return RedisKey.getKey(RedisKey.ROOM_INFO_FORMAT, roomId);
    }

    @Override
    protected Long getExpireSeconds() {
        return -1L;
    }

    @Override
    protected Map<Long, Room> load(List<Long> roomIds) {
        List<Room> rooms = roomDao.listByIds(roomIds);
        return rooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
    }

	public void refresh(Long id) {
		Room room = roomDao.getById(id);
		HashMap<String, Room> map = new HashMap<>();
		map.put(getKey(id), room);
		refresh(map);
	}
}
