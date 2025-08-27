package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.common.cache.PresenceCacheKeyBuilder;
import com.luohuo.flex.common.constant.DefValConstants;
import com.luohuo.flex.im.api.vo.UserRegisterVo;
import com.luohuo.flex.im.common.event.UserRegisterEvent;
import com.luohuo.flex.im.core.chat.service.ContactService;
import com.luohuo.flex.im.core.chat.service.RoomService;
import com.luohuo.flex.im.core.user.service.cache.DefUserCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.validator.utils.AssertUtil;
import com.luohuo.flex.common.cache.common.CaptchaCacheKeyBuilder;
import com.luohuo.flex.im.common.event.UserBlackEvent;
import com.luohuo.flex.im.common.utils.sensitiveword.SensitiveWordBs;
import com.luohuo.flex.im.core.user.dao.BlackDao;
import com.luohuo.flex.im.core.user.dao.ItemConfigDao;
import com.luohuo.flex.im.core.user.dao.UserBackpackDao;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.domain.dto.ItemInfoDTO;
import com.luohuo.flex.im.domain.dto.SummeryInfoDTO;
import com.luohuo.flex.im.domain.entity.Black;
import com.luohuo.flex.im.domain.entity.ItemConfig;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.domain.entity.UserBackpack;
import com.luohuo.flex.im.domain.enums.BlackTypeEnum;
import com.luohuo.flex.im.domain.enums.ItemEnum;
import com.luohuo.flex.im.domain.enums.ItemTypeEnum;
import com.luohuo.flex.model.vo.query.BindEmailReq;
import com.luohuo.flex.im.domain.vo.req.user.BlackReq;
import com.luohuo.flex.im.domain.vo.req.user.ItemInfoReq;
import com.luohuo.flex.im.domain.vo.req.user.ModifyAvatarReq;
import com.luohuo.flex.im.domain.vo.req.user.ModifyNameReq;
import com.luohuo.flex.im.domain.vo.req.user.SummeryInfoReq;
import com.luohuo.flex.im.domain.vo.req.user.WearingBadgeReq;
import com.luohuo.flex.im.domain.vo.resp.user.BadgeResp;
import com.luohuo.flex.im.domain.vo.resp.user.UserInfoResp;
import com.luohuo.flex.im.core.user.service.UserService;
import com.luohuo.flex.im.core.user.service.adapter.UserAdapter;
import com.luohuo.flex.im.core.user.service.cache.ItemCache;
import com.luohuo.flex.im.core.user.service.cache.UserCache;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nyh
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	public static final LocalDateTime MAX_DATE = LocalDateTime.of(2099, 12, 31, 00, 00, 00);
    private final ContactService contactService;
    private final RoomService roomService;
    private UserCache userCache;
	private DefUserCache defUserCache;
    private UserBackpackDao userBackpackDao;
    private UserDao userDao;
    private ItemConfigDao itemConfigDao;
    private ItemCache itemCache;
    private BlackDao blackDao;
	private final CachePlusOps cachePlusOps;
    private UserSummaryCache userSummaryCache;
    private SensitiveWordBs sensitiveWordBs;

	@Override
	public Boolean checkEmail(String email) {
		User user = userDao.getByEmail(email);
		return user != null;
	}

	@Override
	public Long getUIdByUserId(Long defUserId, Long tenantId) {
		try {
			ContextUtil.setTenantId(tenantId);
			return defUserCache.getUserInfo(defUserId).getId();
		} finally {
			ContextUtil.setTenantId(null);
		}
	}

	@Override
	public User getUserById(Long uid) {
		return userDao.getById(uid);
	}

    @Override
    public UserInfoResp getUserInfo(Long uid) {
        User userInfo = userCache.getUserInfo(uid);
        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfoResp(userInfo, countByValidItemId);
    }

    @Override
    @Transactional
    public void modifyInfo(Long uid, ModifyNameReq req) {
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
            userDao.modifyName(uid, req);
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
						user.getAvatarUpdateTime().plusDays(30).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() <=
								LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
				"30天内只能修改一次头像");


        // 更新
		User updateUser = User.builder().avatar(req.getAvatar()).avatarUpdateTime(LocalDateTime.now()).build();
		updateUser.setId(user.getId());
		userDao.updateById(updateUser);
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
		if(ObjectUtil.isNull(req.getDeadline()) || req.getDeadline().equals(0L)){
			user.setDeadline(MAX_DATE);
		} else {
			user.setDeadline(LocalDateTime.now().plusMinutes(req.getDeadline()));
		}
        blackDao.save(user);
        User byId = userDao.getById(uid);
        blackIp(byId.getIpInfo().getCreateIp());
        blackIp(byId.getIpInfo().getUpdateIp());
        SpringUtils.publishEvent(new UserBlackEvent(this, byId));
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
            if (Objects.nonNull(a.getLastModifyTime()) && a.getLastModifyTime() >= TimeUtils.getTime(itemConfig.getUpdateTime())) {
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
		CacheResult<String> cacheResult = cachePlusOps.hGet(CaptchaCacheKeyBuilder.hashBuild("emailCode", req.getUuid()));
		String emailCode = cacheResult.getValue();
		if (StrUtil.isEmpty(emailCode)) {
			throw new BizException("验证码已过期");
		}

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
			cachePlusOps.hDel("emailCode", req.getUuid());
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

	public Boolean subElectricity(Long uid) {
		User user = userDao.getById(uid);
		UpdateWrapper<User> uw = new UpdateWrapper<>();
		uw.lambda().set(User::getNum, user.getNum() - 1).eq(User::getId, user.getId());
		boolean success = userDao.update(uw);
		if(success){
			userSummaryCache.delete(uid);
		}
		return success;
	}

    @Override
    @Transactional
    public Boolean register(UserRegisterVo userRegisterVo) {
        // 1. 检查邮箱是否已被其他用户绑定
		ContextUtil.setTenantId(userRegisterVo.getTenantId());
        if (userDao.existsByEmailAndIdNot(null, userRegisterVo.getEmail())) {
			return false;
        }
        String account = userRegisterVo.getEmail().split("@")[0];
        boolean exists = userDao.count(new QueryWrapper<User>().lambda().eq(User::getAccount, account)) > 0;

        // 2. 走注册流程
		final User newUser = User.builder()
                .userId(userRegisterVo.getUserId())
                .avatar(userRegisterVo.getAvatar())
                .account(exists? userRegisterVo.getEmail(): account)
                .email(userRegisterVo.getEmail())
				.sex(userRegisterVo.getSex())
				.userType(userRegisterVo.getUserType())
                .name(userRegisterVo.getName())
				.resume("这个人还没有填写个人简介呢")
                .openId(userRegisterVo.getOpenId())
				.tenantId(userRegisterVo.getTenantId())
                .build();

        // 保存用户
		newUser.setCreateBy(1L);
        userDao.save(newUser);
        // 创建会话
        contactService.createContact(newUser.getId(), DefValConstants.DEF_ROOM_ID);
        // 创建群成员
        roomService.createGroupMember(DefValConstants.DEF_GROUP_ID, newUser.getId());

		// 注入群组信息
		cachePlusOps.sAdd(PresenceCacheKeyBuilder.groupMembersKey(DefValConstants.DEF_ROOM_ID), newUser.getId());
		cachePlusOps.sAdd(PresenceCacheKeyBuilder.userGroupsKey(newUser.getId()), DefValConstants.DEF_ROOM_ID);

        // 发布用户注册消息
        SpringUtils.publishEvent(new UserRegisterEvent(this, newUser));
		return true;
    }
}
