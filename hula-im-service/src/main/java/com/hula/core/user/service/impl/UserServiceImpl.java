package com.hula.core.user.service.impl;

import com.hula.common.utils.AssertUtil;
import com.hula.core.user.dao.ItemConfigDao;
import com.hula.core.user.dao.UserBackpackDao;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.ItemConfig;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.entity.UserBackpack;
import com.hula.core.user.domain.enums.ItemEnum;
import com.hula.core.user.domain.enums.ItemTypeEnum;
import com.hula.core.user.domain.vo.resp.BadgeResp;
import com.hula.core.user.domain.vo.resp.UserInfoResp;
import com.hula.core.user.service.UserService;
import com.hula.core.user.service.adapter.UserAdapter;
import com.hula.core.user.service.cache.ItemCache;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nyh
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private UserBackpackDao userBackpackDao;
    private ItemCache itemCache;
    private ItemConfigDao itemConfigDao;

    @Override
    @Transactional
    public Long register(User insert) {
        userDao.save(insert);
        // TODO 用户注册事件 (nyh -> 2024-04-10 00:31:06)
        return insert.getId();
    }

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User user = userDao.getById(uid);
        Integer modifyNameCount = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfo(user, modifyNameCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyName(Long uid, String name) {
        User oldUser = userDao.getByName(name);
        AssertUtil.isEmpty(oldUser, "名字已被抢占了，请换一个");
        UserBackpack modifyNameItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(modifyNameItem, "改名卡不足，等待后续活动领取哦~");
        // 使用改名卡
        boolean success = userBackpackDao.useItem(modifyNameItem);
        if (success) {
            userDao.modifyName(uid, name);
        }
    }

    @Override
    public List<BadgeResp> badges(Long uid) {
        //查询所有的徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        //查询用户拥有的徽章
        List<UserBackpack> userBackpacks = userBackpackDao.getByItemIds(uid ,itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        //用户当前佩戴的徽章
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(itemConfigs, userBackpacks, user);
    }

    @Override
    public void wearingBadge(Long uid, Long itemId) {
        // 确保有徽章可佩戴
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, itemId);
        AssertUtil.isNotEmpty(firstValidItem, "你还未拥有此徽章哦~");
        // 确保该物品是徽章
        ItemConfig itemConfig = itemConfigDao.getById(firstValidItem.getItemId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "只有徽章才可以佩戴哦~");
        userDao.wearingBadge(uid, itemId);
    }
}
