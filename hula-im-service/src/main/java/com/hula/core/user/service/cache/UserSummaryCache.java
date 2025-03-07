package com.hula.core.user.service.cache;

import com.hula.common.constant.RedisKey;
import com.hula.common.service.cache.AbstractRedisStringCache;
import com.hula.core.user.dao.UserBackpackDao;
import com.hula.core.user.domain.dto.SummeryInfoDTO;
import com.hula.core.user.domain.entity.*;
import com.hula.core.user.domain.enums.ItemTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户所有信息的缓存
 * @author nyh
 */
@Component
public class UserSummaryCache extends AbstractRedisStringCache<Long, SummeryInfoDTO> {
    @Resource
    private UserInfoCache userInfoCache;
    @Resource
    private UserBackpackDao userBackpackDao;
    @Resource
    private ItemCache itemCache;

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
        Map<Long, User> userMap = userInfoCache.getBatch(uidList);
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
			summeryInfoDTO.setAccountCode(user.getAccountCode());
			summeryInfoDTO.setUserStateId(user.getUserStateId());
			summeryInfoDTO.setLocPlace(Optional.ofNullable(user.getIpInfo()).map(IpInfo::getUpdateIpDetail).map(IpDetail::getCity).orElse(null));
            summeryInfoDTO.setWearingItemId(user.getItemId());
            summeryInfoDTO.setItemIds(userBackpacks.stream().map(UserBackpack::getItemId).collect(Collectors.toList()));
            return summeryInfoDTO;
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SummeryInfoDTO::getUid, Function.identity()));
    }
}
