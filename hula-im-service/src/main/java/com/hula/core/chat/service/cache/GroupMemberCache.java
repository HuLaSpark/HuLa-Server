package com.hula.core.chat.service.cache;

import com.hula.core.chat.dao.GroupMemberDao;
import com.hula.core.chat.dao.RoomGroupDao;
import com.hula.core.chat.domain.entity.RoomGroup;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 群成员相关缓存
 * @author nyh
 */
@Component
public class GroupMemberCache {

    @Resource
    private RoomGroupDao roomGroupDao;
    @Resource
    private GroupMemberDao groupMemberDao;

	/**
	 * 群里所有人
	 * @param roomId
	 * @return
	 */
    @Cacheable(cacheNames = "member", key = "'groupMember'+#roomId")
    public List<Long> getMemberUidList(Long roomId) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        if (Objects.isNull(roomGroup)) {
            return null;
        }
        return groupMemberDao.getMemberUidList(roomGroup.getId(), null);
    }

	/**
	 * 这里不查询屏蔽群的人
	 * @param roomId
	 * @return
	 */
	@Cacheable(cacheNames = "member", key = "'groupExceptMember:'+#roomId")
	public List<Long> getMemberExceptUidList(Long roomId) {
		RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
		if (Objects.isNull(roomGroup)) {
			return null;
		}
		return groupMemberDao.getMemberUidList(roomGroup.getId(), false);
	}

	/**
	 * 这里的操作要双清
	 * @param roomId
	 */
	public void evictMemberUidList(Long roomId) {
		evictMemberList(roomId);
		evictExceptMemberList(roomId);
	}


	@CacheEvict(cacheNames = "member", key = "'groupExceptMember:'+#roomId")
	public List<Long> evictExceptMemberList(Long roomId) {
		return null;
	}

	@CacheEvict(cacheNames = "member", key = "'groupMember:'+#roomId")
	public List<Long> evictMemberList(Long roomId) {
		return null;
	}
}
