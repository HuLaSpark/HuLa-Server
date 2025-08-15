package com.luohuo.flex.im.core.chat.service.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.flex.im.common.constant.RedisKey;
import com.luohuo.flex.im.common.service.cache.AbstractRedisStringCache;
import com.luohuo.flex.im.core.chat.dao.AnnouncementsReadRecordDao;
import com.luohuo.flex.im.domain.entity.AnnouncementsReadRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 群公告已读数量的缓存
 * @author nyh
 */
@Component
@RequiredArgsConstructor
public class RoomAnnouncementsCache extends AbstractRedisStringCache<Long, Long> {
    private final AnnouncementsReadRecordDao dao;
	private final CachePlusOps cachePlusOps;

	public Long add(Long announcementId, Long item) {
		return cachePlusOps.sAdd(new CacheKey(getKey(announcementId)), item);
	}

	public Long get(Long announcementId) {
		return cachePlusOps.sSize(getKey(announcementId));
	}

    @Override
    protected String getKey(Long id) {
        return RedisKey.getKey(RedisKey.GROUP_ANNOUNCEMENTS_FORMAT, id);
    }

    @Override
    protected Long getExpireSeconds() {
        return 7 * 60L * 60L * 24;
    }

    public Map<Long, Long> load(List<Long> ids) {
		List<AnnouncementsReadRecord> list = dao.listByIds(ids);
		if (list.isEmpty()) {
			return new HashMap<>();
		}
		Map<Long, Long> map = list.stream().collect(Collectors.groupingBy(AnnouncementsReadRecord::getAnnouncementsId, Collectors.summingLong(record -> 1L)));
		for (Long key : map.keySet()) {
			add(key, map.get(key));
		}
		return map;
    }
}
