package com.hula.core.chat.service.cache;

import com.hula.core.chat.dao.GroupMemberDao;
import com.hula.core.chat.dao.MessageDao;
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
    private MessageDao messageDao;
    @Resource
    private RoomGroupDao roomGroupDao;
    @Resource
    private GroupMemberDao groupMemberDao;

    @Cacheable(cacheNames = "member", key = "'groupMember'+#roomId")
    public List<Long> getMemberUidList(Long roomId) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        if (Objects.isNull(roomGroup)) {
            return null;
        }
        return groupMemberDao.getMemberUidList(roomGroup.getId());
    }

    @CacheEvict(cacheNames = "member", key = "'groupMember'+#roomId")
    public List<Long> evictMemberUidList(Long roomId) {
        return null;
    }

}
