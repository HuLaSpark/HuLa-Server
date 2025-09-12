package com.luohuo.flex.im.core.user.service.cache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luohuo.flex.im.common.constant.RedisKey;
import com.luohuo.flex.im.common.service.cache.AbstractRedisStringCache;
import com.luohuo.flex.im.core.user.dao.BlackDao;
import com.luohuo.flex.im.core.user.dao.UserBackpackDao;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.core.user.dao.UserRoleDao;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.im.domain.entity.Black;
import com.luohuo.flex.im.domain.entity.ItemConfig;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.domain.entity.UserBackpack;
import com.luohuo.flex.im.domain.entity.UserRole;
import com.luohuo.flex.im.domain.enums.ItemTypeEnum;
import com.luohuo.flex.im.domain.vo.response.ChatMemberListResp;
import com.luohuo.flex.model.entity.base.IpDetail;
import com.luohuo.flex.model.entity.base.IpInfo;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户所有信息的缓存
 * @author nyh
 */
@Component
@AllArgsConstructor
public class UserSummaryCache extends AbstractRedisStringCache<Long, SummeryInfoDTO> {
	private final UserDao userDao;
    private final UserCache userCache;
    private final UserBackpackDao userBackpackDao;
    private final ItemCache itemCache;
	private final UserRoleDao userRoleDao;
	private final BlackDao blackDao;

	/**
	 * 每小时一执行
	 */
	@Scheduled(cron = "0 0 * * * ?")
	public void cleanExpiredBlacks() {
		evictBlackMap();
	}

    @Override
    protected String getKey(Long uid) {
        return RedisKey.getKey(RedisKey.USER_SUMMARY_FORMAT, uid);
    }

    @Override
    protected Long getExpireSeconds() {
        return 10 * 60L;
    }

    @Override
    protected Map<Long, SummeryInfoDTO> load(List<Long> uidList) {//后续可优化徽章信息也异步加载
        //用户基本信息
        Map<Long, User> userMap = userCache.getBatch(uidList);
        //用户徽章信息
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        List<Long> itemIds = itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList());
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uidList, itemIds);
        Map<Long, List<UserBackpack>> userBadgeMap = backpacks.stream().collect(Collectors.groupingBy(UserBackpack::getUid));
        //用户最后一次更新时间
        return uidList.stream().map(uid -> {
            SummeryInfoDTO summeryInfoDTO = new SummeryInfoDTO();
            User user = userMap.get(uid);
            if (Objects.isNull(user)) {
                return null;
            }
            List<UserBackpack> userBackpacks = userBadgeMap.getOrDefault(user.getId(), new ArrayList<>());
            summeryInfoDTO.setUid(user.getId());
            summeryInfoDTO.setName(user.getName());
            summeryInfoDTO.setAvatar(user.getAvatar());
			summeryInfoDTO.setAccount(user.getAccount());
			summeryInfoDTO.setUserStateId(user.getUserStateId());
			summeryInfoDTO.setLocPlace(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getUpdateIpDetail).map(IpDetail::getCity).orElse(null));
            summeryInfoDTO.setWearingItemId(user.getItemId());
            summeryInfoDTO.setItemIds(userBackpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toList()));
			summeryInfoDTO.setUserType(user.getUserType());
			summeryInfoDTO.setEmail(user.getEmail());
			summeryInfoDTO.setOpenId(user.getOpenId());
			summeryInfoDTO.setSex(user.getSex());
			summeryInfoDTO.setResume(user.getResume());
			summeryInfoDTO.setLastOptTime(user.getLastOptTime());
            return summeryInfoDTO;
        }).filter(Objects::nonNull).collect(Collectors.toMap(SummeryInfoDTO::getUid, Function.identity()));
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

	@Cacheable(cacheNames = "luohuo:user", key = "'roles:'+#uid")
	public Set<Long> getRoleSet(Long uid) {
		List<UserRole> userRoles = userRoleDao.listByUid(uid);
		return userRoles.stream()
				.map(UserRole::getRoleId)
				.collect(Collectors.toSet());
	}

	@CacheEvict(cacheNames = "luohuo:user", key = "'roles'")
	public Map<Integer, Set<String>> evictrolesSet() {
		return null;
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
}
