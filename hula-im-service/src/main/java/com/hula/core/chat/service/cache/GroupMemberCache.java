package com.hula.core.chat.service.cache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hula.core.chat.dao.GroupMemberDao;
import com.hula.core.chat.dao.RoomGroupDao;
import com.hula.core.chat.domain.entity.GroupMember;
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
	 * 获取指定成员在群中的详细信息
	 * @param roomId 聊天室ID
	 * @param memberUid 成员用户ID
	 * @return 成员详细信息
	 */
	@Cacheable(cacheNames = "memberDetail", key = "'groupMemberDetail:' + #roomId + ':' + #memberUid")
	public GroupMember getMemberDetail(Long roomId, Long memberUid) {
		return groupMemberDao.getBaseMapper().selectOne(new QueryWrapper<GroupMember>()
				.eq("group_id", roomId)
				.eq("uid", memberUid)
				.last("LIMIT 1")
		);
	}

	@CacheEvict(cacheNames = "memberDetail", key = "'groupMemberDetail:' + #roomId + ':' + #memberUid")
	public void evictMemberDetail(Long roomId, Long memberUid) {
		// 清理单个成员缓存
	}

	@CacheEvict(cacheNames = "memberDetail", allEntries = true)
	public void evictAllMemberDetails() {
		// 清理所有成员详情缓存（慎用）
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
