package com.luohuo.flex.im.core.user.service.cache;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.im.common.constant.RedisKey;
import com.luohuo.flex.im.domain.vo.response.ChatMemberListResp;
import com.luohuo.flex.im.core.user.dao.BlackDao;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.core.user.dao.UserRoleDao;
import com.luohuo.flex.im.domain.entity.Black;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.domain.entity.UserRole;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 乾乾
 */
@Component
@RequiredArgsConstructor
public class UserCache {

    private final UserDao userDao;
    private final BlackDao blackDao;
    private final UserRoleDao userRoleDao;
    private final UserSummaryCache userSummaryCache;
	private final CachePlusOps cachePlusOps;

	/**
	 * 每小时一执行
	 */
	@Scheduled(cron = "0 0 * * * ?")
	public void cleanExpiredBlacks() {
		evictBlackMap();
	}

    public List<Long> getUserModifyTime(List<Long> uidList) {
		List<String> keys = uidList.stream().map(uid -> RedisKey.getKey(RedisKey.USER_MODIFY_FORMAT, uid)).collect(Collectors.toList());
		return cachePlusOps.mGet(keys, Long.class);
    }

    public void refreshUserModifyTime(Long uid) {
		String key = RedisKey.getKey(RedisKey.USER_MODIFY_FORMAT, uid);
		cachePlusOps.set(new CacheKey(key), TimeUtils.getTime());
    }

    /**
     * 获取用户信息，盘路缓存模式
     */
    public User getUserInfo(Long uid) {//todo 后期做二级缓存
        return getUserInfoBatch(Collections.singleton(uid)).get(uid);
    }

    /**
     * 获取用户信息，盘路缓存模式
     */
    public Map<Long, User> getUserInfoBatch(Set<Long> uids) {
        //批量组装key
        List<String> keys = uids.stream().map(a -> RedisKey.getKey(RedisKey.USER_INFO_FORMAT, a)).collect(Collectors.toList());
        //批量get
        List<User> mget = cachePlusOps.mGet(keys, User.class);
        Map<Long, User> map = mget.stream().filter(Objects::nonNull).collect(Collectors.toMap(User::getId, Function.identity()));
        //发现差集——还需要load更新的uid
        List<Long> needLoadUidList = uids.stream().filter(a -> !map.containsKey(a)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(needLoadUidList)) {
            //批量load
            List<User> needLoadUserList = userDao.listByIds(needLoadUidList);
            Map<String, User> redisMap = needLoadUserList.stream().collect(Collectors.toMap(a -> RedisKey.getKey(RedisKey.USER_INFO_FORMAT, a.getId()), Function.identity()));
			cachePlusOps.mSet(redisMap, 5 * 60);
            //加载回redis
            map.putAll(needLoadUserList.stream().collect(Collectors.toMap(User::getId, Function.identity())));
        }
        return map;
    }

    /**
     * 清空用户缓存，下次请求最新数据
     * @param uid 用户id
     **/
    public void userInfoChange(Long uid) {
		cachePlusOps.del(RedisKey.getKey(RedisKey.USER_INFO_FORMAT, uid));

        //删除UserSummaryCache，前端下次懒加载的时候可以获取到最新的数据
        userSummaryCache.delete(uid);
        refreshUserModifyTime(uid);
    }

    @Cacheable(cacheNames = "luohuo:user", key = "'blackList'")
    public Map<Integer, Set<String>> getBlackMap() {
		LocalDateTime now = LocalDateTime.now();
		Map<Integer, List<Black>> collect = blackDao.getBaseMapper().selectList(new QueryWrapper<Black>().gt("deadline", now)).stream().collect(Collectors.groupingBy(Black::getType));
        Map<Integer, Set<String>> result = new HashMap<>(collect.size());
        for (Map.Entry<Integer, List<Black>> entry : collect.entrySet()) {
            result.put(entry.getKey(), entry.getValue().stream().map(Black::getTarget).collect(Collectors.toSet()));
        }
        return result;
    }

    @CacheEvict(cacheNames = "luohuo:user", key = "'blackList'")
    public Map<Integer, Set<String>> evictBlackMap() {
        return null;
    }

    @Cacheable(cacheNames = "luohuo:user", key = "'roles'+#uid")
    public Set<Long> getRoleSet(Long uid) {
        List<UserRole> userRoles = userRoleDao.listByUid(uid);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());
    }


	/**
	 * 根据key查询好友
	 * @param key
	 * @return
	 */
	@Cacheable(cacheNames = "luohuo:user", key = "'findFriend:'+#key")
	public List<ChatMemberListResp> getFriend(String key) {
		return userDao.getFriend(key);
	}

	/**
	 * 当 key 的数据改变后需要调用此方法
	 * @param key
	 * @return
	 */
	@CacheEvict(cacheNames = "luohuo:user", key = "'findFriend:'+#key")
	public List<Long> evictFriend(String key) {
		return null;
	}
}
