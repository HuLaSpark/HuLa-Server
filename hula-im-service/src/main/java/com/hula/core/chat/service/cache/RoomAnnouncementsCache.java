package com.hula.core.chat.service.cache;

import com.hula.common.constant.RedisKey;
import com.hula.common.service.cache.AbstractRedisStringCache;
import com.hula.core.chat.dao.AnnouncementsReadRecordDao;
import com.hula.core.chat.domain.entity.AnnouncementsReadRecord;
import com.hula.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 群公告已读数量的缓存
 * @author nyh
 */
@Component
public class RoomAnnouncementsCache extends AbstractRedisStringCache<Long, Long> {
    @Resource
    private AnnouncementsReadRecordDao dao;

	public Long add(Long announcementId, Long item) {
		return RedisUtils.sSet(getKey(announcementId), item);
	}

	public Long get(Long announcementId) {
		return RedisUtils.sGetSetSize(getKey(announcementId));
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
