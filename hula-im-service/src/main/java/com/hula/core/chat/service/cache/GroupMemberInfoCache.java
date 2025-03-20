package com.hula.core.chat.service.cache;

import com.hula.common.constant.RedisKey;
import com.hula.common.service.cache.AbstractRedisStringCache;
import com.hula.core.chat.dao.GroupMemberDao;
import com.hula.core.chat.domain.entity.GroupMember;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 群成员基本信息的缓存
 * @author 乾乾
 */
@Component
public class GroupMemberInfoCache extends AbstractRedisStringCache<Long, GroupMember> {
    @Resource
    private GroupMemberDao groupMemberDao;

    @Override
    protected String getKey(Long id) {
        return RedisKey.getKey(RedisKey.GROUP_MEMBER_INFO_FORMAT, id);
    }

    @Override
    protected Long getExpireSeconds() {
        return 5 * 60L;
    }

    @Override
    protected Map<Long, GroupMember> load(List<Long> uidList) {
        List<GroupMember> needLoadUserList = groupMemberDao.listByIds(uidList);
        return needLoadUserList.stream().collect(Collectors.toMap(GroupMember::getId, Function.identity()));
    }
}
