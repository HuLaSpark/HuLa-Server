package com.hula.core.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.hula.common.event.UserBlackEvent;
import com.hula.common.event.UserRegisterEvent;
import com.hula.common.utils.AssertUtil;
import com.hula.core.user.dao.BlackDao;
import com.hula.core.user.dao.ItemConfigDao;
import com.hula.core.user.dao.UserBackpackDao;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.*;
import com.hula.core.user.domain.enums.BlackTypeEnum;
import com.hula.core.user.domain.enums.ItemEnum;
import com.hula.core.user.domain.enums.ItemTypeEnum;
import com.hula.core.user.domain.vo.req.BlackReq;
import com.hula.core.user.domain.vo.resp.BadgeResp;
import com.hula.core.user.domain.vo.resp.UserInfoResp;
import com.hula.core.user.service.UserService;
import com.hula.core.user.service.adapter.UserAdapter;
import com.hula.core.user.service.cache.ItemCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author nyh
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private UserBackpackDao userBackpackDao;
    private ItemCache itemCache;
    private ItemConfigDao itemConfigDao;
    private ApplicationEventPublisher applicationEventPublisher;
    private BlackDao blackDao;

    @Override
    public Long register(User user) {
        userDao.save(user);
        applicationEventPublisher.publishEvent(new UserRegisterEvent(this, user));
        return user.getId();
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

    @Override
    public void black(BlackReq req) {
        Long uid = req.getUid();
        Black user = new Black();
        user.setTarget(uid.toString());
        user.setType(BlackTypeEnum.UID.getType());
        blackDao.save(user);
        User byId = userDao.getById(uid);
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getCreateIp).orElse(null));
        blackIp(Optional.ofNullable(byId.getIpInfo()).map(IpInfo::getUpdateIp).orElse(null));
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, byId));
    }

    public void blackIp(String ip) {
        if (StrUtil.isBlank(ip)) {
            return;
        }
        try {
            Black user = new Black();
            user.setTarget(ip);
            user.setType(BlackTypeEnum.IP.getType());
            blackDao.save(user);
        } catch (Exception e) {
            log.error("duplicate black ip:{}", ip);
        }
    }
}
