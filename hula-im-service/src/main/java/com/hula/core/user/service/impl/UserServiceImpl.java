package com.hula.core.user.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.hula.common.event.UserBlackEvent;
import com.hula.common.utils.sensitiveword.SensitiveWordBs;
import com.hula.core.user.dao.BlackDao;
import com.hula.core.user.dao.ItemConfigDao;
import com.hula.core.user.dao.UserBackpackDao;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.dto.ItemInfoDTO;
import com.hula.core.user.domain.dto.SummeryInfoDTO;
import com.hula.core.user.domain.entity.Black;
import com.hula.core.user.domain.entity.ItemConfig;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.entity.UserBackpack;
import com.hula.core.user.domain.enums.BlackTypeEnum;
import com.hula.core.user.domain.enums.ItemEnum;
import com.hula.core.user.domain.enums.ItemTypeEnum;
import com.hula.core.user.domain.vo.req.user.*;
import com.hula.core.user.domain.vo.resp.user.BadgeResp;
import com.hula.core.user.domain.vo.resp.user.UserInfoResp;
import com.hula.core.user.service.UserService;
import com.hula.core.user.service.adapter.UserAdapter;
import com.hula.core.user.service.cache.ItemCache;
import com.hula.core.user.service.cache.UserCache;
import com.hula.core.user.service.cache.UserSummaryCache;
import com.hula.exception.BizException;
import com.hula.utils.AssertUtil;
import com.hula.utils.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author nyh
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserCache userCache;
    private UserBackpackDao userBackpackDao;
    private UserDao userDao;
    private ItemConfigDao itemConfigDao;
    private ApplicationEventPublisher applicationEventPublisher;
    private ItemCache itemCache;
    private BlackDao blackDao;
    private UserSummaryCache userSummaryCache;
    private SensitiveWordBs sensitiveWordBs;

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User userInfo = userCache.getUserInfo(uid);
        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfoResp(userInfo, countByValidItemId);
    }

    @Override
    @Transactional
    public void modifyName(Long uid, ModifyNameReq req) {
        // 判断名字是不是重复
        String newName = req.getName();
        AssertUtil.isFalse(sensitiveWordBs.hasSensitiveWord(newName), "名字中包含敏感词，请重新输入"); // 判断名字中有没有敏感词
        // 名称可以重复
//        User oldUser = userDao.getByName(newName);
//        AssertUtil.isEmpty(oldUser, "名字已经被抢占了，请换一个哦~~");
        // 判断改名卡够不够
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        AssertUtil.isNotEmpty(firstValidItem, "改名次数不够了，等后续活动送改名卡哦");
        // 使用改名卡
        boolean useSuccess = userBackpackDao.invalidItem(firstValidItem.getId());
        // 用乐观锁，就不用分布式锁了
        if (useSuccess) {
            // 改名
            userDao.modifyName(uid, req.getName());
            // 删除缓存
			userSummaryCache.delete(uid);
            userCache.userInfoChange(uid);
			userCache.evictFriend(userCache.getUserInfo(uid).getAccount());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void modifyAvatar(Long uid, ModifyAvatarReq req) {
        // 判断30天内是否改过头像
        User user = userDao.getById(uid);
        AssertUtil.isTrue(Objects.isNull(user.getAvatarUpdateTime()) ||
                DateUtils.addDays(user.getAvatarUpdateTime(), 30).getTime() <=
                        Calendar.getInstance().getTime().getTime(), "30天内只能修改一次头像");
        // 更新
        userDao.updateById(User.builder().id(user.getId()).avatar(req.getAvatar()).avatarUpdateTime(DateUtil.date()).build());
        // 删除缓存
        userCache.userInfoChange(uid);
		userSummaryCache.delete(uid);
		userCache.evictFriend(user.getAccount());
    }

    @Override
    public List<BadgeResp> badges(Long uid) {
        // 查询所有徽章
        List<ItemConfig> itemConfigs = itemCache.getByType(ItemTypeEnum.BADGE.getType());
        // 查询用户拥有的徽章
        List<UserBackpack> backpacks = userBackpackDao.getByItemIds(uid, itemConfigs.stream().map(ItemConfig::getId).collect(Collectors.toList()));
        // 查询用户当前佩戴的标签
        User user = userDao.getById(uid);
        return UserAdapter.buildBadgeResp(itemConfigs, backpacks, user);
    }

    @Override
    public void wearingBadge(Long uid, WearingBadgeReq req) {
        // 确保有这个徽章
        UserBackpack firstValidItem = userBackpackDao.getFirstValidItem(uid, req.getBadgeId());
        AssertUtil.isNotEmpty(firstValidItem, "您没有这个徽章哦，快去达成条件获取吧");
        // 确保物品类型是徽章
        ItemConfig itemConfig = itemConfigDao.getById(firstValidItem.getItemId());
        AssertUtil.equal(itemConfig.getType(), ItemTypeEnum.BADGE.getType(), "该徽章不可佩戴");
        // 佩戴徽章
        userDao.wearingBadge(uid, req.getBadgeId());
        // 删除用户缓存
        userCache.userInfoChange(uid);
		userSummaryCache.delete(uid);
    }

    @Override
    public void black(BlackReq req) {
        Long uid = req.getUid();
        Black user = new Black();
        user.setTarget(uid.toString());
        user.setType(BlackTypeEnum.UID.getType());
        blackDao.save(user);
        User byId = userDao.getById(uid);
        blackIp(byId.getIpInfo().getCreateIp());
        blackIp(byId.getIpInfo().getUpdateIp());
        applicationEventPublisher.publishEvent(new UserBlackEvent(this, byId));
    }

    @Override
    public List<SummeryInfoDTO> getSummeryUserInfo(SummeryInfoReq req) {
        //需要前端同步的uid
        List<Long> uidList = getNeedSyncUidList(req.getReqList());
        //加载用户信息
        Map<Long, SummeryInfoDTO> batch = userSummaryCache.getBatch(uidList);
        return req.getReqList()
                .stream()
                .map(a -> batch.containsKey(a.getUid()) ? batch.get(a.getUid()) : SummeryInfoDTO.skip(a.getUid()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemInfoDTO> getItemInfo(ItemInfoReq req) {//简单做，更新时间可判断被修改
        return req.getReqList().stream().map(a -> {
            ItemConfig itemConfig = itemCache.getById(a.getItemId());
            if (Objects.nonNull(a.getLastModifyTime()) && a.getLastModifyTime() >= itemConfig.getUpdateTime().getTime()) {
                return ItemInfoDTO.skip(a.getItemId());
            }
            ItemInfoDTO dto = new ItemInfoDTO();
            dto.setItemId(itemConfig.getId());
            dto.setImg(itemConfig.getImg());
            dto.setDescribe(itemConfig.getDescribe());
            return dto;
        }).collect(Collectors.toList());
    }

	/**
	 * 聊天用的用户更新状态
	 * @param uid
	 * @param userStateId
	 * @return
	 */
	@Override
	public Boolean changeUserState(Long uid, Long userStateId){
		return userDao.changeUserState(uid, userStateId) > 0;
	}

	@Override
	public Boolean bindEmail(Long uid, BindEmailReq req) {
		// 1.校验验证码
		String emailCode = RedisUtils.hget("emailCode", req.getUuid()).toString();
		if(StrUtil.isEmpty(emailCode) || !emailCode.equals(req.getCode())){
			throw new BizException("验证码错误!");
		}

		// 2. 检查邮箱是否已被其他用户绑定
		if (userDao.existsByEmailAndIdNot(uid, req.getEmail())) {
			throw new BizException("该邮箱已被其他账号绑定");
		}

		// 3. 修改邮箱
		User userInfo = userCache.getUserInfo(uid);
		userInfo.setEmail(req.getEmail());
		boolean save = userDao.save(userInfo);
		if(save){
            RedisUtils.hdel("emailCode", req.getUuid());
			userCache.userInfoChange(uid);
			userSummaryCache.delete(uid);
		}
		return save;
	}

	private List<Long> getNeedSyncUidList(List<SummeryInfoReq.infoReq> reqList) {
        List<Long> needSyncUidList = new ArrayList<>();
        List<Long> userModifyTime = userCache.getUserModifyTime(reqList.stream().map(SummeryInfoReq.infoReq::getUid).collect(Collectors.toList()));
        for (int i = 0; i < reqList.size(); i++) {
            SummeryInfoReq.infoReq infoReq = reqList.get(i);
            Long modifyTime = userModifyTime.get(i);
            if (Objects.isNull(infoReq.getLastModifyTime()) || (Objects.nonNull(modifyTime) && modifyTime > infoReq.getLastModifyTime())) {
                needSyncUidList.add(infoReq.getUid());
            }
        }
        return needSyncUidList;
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
