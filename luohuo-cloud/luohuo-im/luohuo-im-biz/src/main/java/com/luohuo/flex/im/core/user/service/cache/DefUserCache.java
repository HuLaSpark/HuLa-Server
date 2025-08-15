package com.luohuo.flex.im.core.user.service.cache;

import cn.hutool.core.collection.CollUtil;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.flex.im.common.constant.RedisKey;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
public class DefUserCache {

    private final UserDao userDao;
	private final CachePlusOps cachePlusOps;

    /**
     * 获取用户信息，盘路缓存模式
     */
    public User getUserInfo(Long defUserId) {//todo 后期做二级缓存
        return getUserInfoBatch(Collections.singleton(defUserId)).get(defUserId);
    }

    /**
     * 获取用户信息，盘路缓存模式
     */
    public Map<Long, User> getUserInfoBatch(Set<Long> uids) {
        //批量组装key
        List<String> keys = uids.stream().map(a -> RedisKey.getKey(RedisKey.DEF_USER_INFO_FORMAT, a)).collect(Collectors.toList());
        //批量get
        List<User> mget = cachePlusOps.mGet(keys, User.class);
        Map<Long, User> map = mget.stream().filter(Objects::nonNull).collect(Collectors.toMap(User::getUserId, Function.identity()));
        //发现差集——还需要load更新的uid
        List<Long> needLoadUidList = uids.stream().filter(a -> !map.containsKey(a)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(needLoadUidList)) {
            //批量load
            List<User> needLoadUserList = userDao.getByDefUserId(needLoadUidList);
            Map<String, User> redisMap = needLoadUserList.stream().collect(Collectors.toMap(a -> RedisKey.getKey(RedisKey.DEF_USER_INFO_FORMAT, a.getUserId()), Function.identity()));
			cachePlusOps.mSet(redisMap, 5 * 60);
            //加载回redis
            map.putAll(needLoadUserList.stream().collect(Collectors.toMap(User::getUserId, Function.identity())));
        }
        return map;
    }
}
