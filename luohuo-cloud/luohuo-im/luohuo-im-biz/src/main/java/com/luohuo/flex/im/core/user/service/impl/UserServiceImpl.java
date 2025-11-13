package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.common.cache.PresenceCacheKeyBuilder;
import com.luohuo.flex.common.constant.DefValConstants;
import com.luohuo.flex.im.api.vo.UserRegisterVo;
import com.luohuo.flex.im.common.event.UserRegisterEvent;
import com.luohuo.flex.im.core.chat.service.RoomAppService;
import com.luohuo.flex.im.core.user.service.cache.DefUserCache;
import com.luohuo.flex.im.core.user.service.cache.ItemCache;
import com.luohuo.flex.im.core.user.service.cache.UserCache;
import com.luohuo.flex.model.entity.base.IpInfo;
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
import com.luohuo.flex.im.domain.vo.req.user.WearingBadgeReq;
import com.luohuo.flex.im.domain.vo.resp.user.BadgeResp;
import com.luohuo.flex.im.domain.vo.resp.user.UserInfoResp;
import com.luohuo.flex.im.core.user.service.UserService;
import com.luohuo.flex.im.core.user.service.adapter.UserAdapter;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
	private final RoomAppService roomAppService;
	private final DefUserCache defUserCache;
    private final UserBackpackDao userBackpackDao;
    private final UserDao userDao;
    private final ItemConfigDao itemConfigDao;
    private final ItemCache itemCache;
    private final BlackDao blackDao;
	private final CachePlusOps cachePlusOps;
	private final UserCache userCache;
    private final UserSummaryCache userSummaryCache;
    private final SensitiveWordBs sensitiveWordBs;

	@Override
	public Boolean refreshIpInfo(Long uid, IpInfo ipInfo) {
		User user = new User();
		user.setId(uid);
		user.setIpInfo(ipInfo);
		boolean updated = userDao.updateById(user);

		// 清空缓存
		userCache.delete(uid);
		userSummaryCache.delete(uid);
		return updated;
	}

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
		SummeryInfoDTO userInfo = userSummaryCache.get(uid);
        Integer countByValidItemId = userBackpackDao.getCountByValidItemId(uid, ItemEnum.MODIFY_NAME_CARD.getId());
        return UserAdapter.buildUserInfoResp(userInfo, countByValidItemId);
    }

	@Override
	public List<SummeryInfoDTO> getUserInfo(List<Long> uidList) {
		return new ArrayList<>(userSummaryCache.getBatch(uidList).values());
	}

    @Override
    @Transactional
    public void modifyInfo(Long uid, ModifyNameReq req) {
        // 判断名字是不是重复
        String newName = req.getName();
        AssertUtil.isFalse(sensitiveWordBs.hasSensitiveWord(newName), "名字中包含敏感词，请重新输入"); // 判断名字中有没有敏感词

		User user = userDao.getById(uid);
//		AssertUtil.isTrue(req.getAvatar().equals(user.getAvatar()) ||
//						(user.getAvatarUpdateTime() != null && user.getAvatarUpdateTime().plusDays(30).isBefore(LocalDateTime.now())),
//				"30天内只能修改一次头像");
		// 更新
		User update = new User();
		update.setId(uid);
		update.setSex(req.getSex());
		update.setName(req.getName());
		update.setResume(req.getResume());

		if(StrUtil.isNotEmpty(req.getAvatar()) && !req.getAvatar().equals(user.getAvatar())){
			update.setAvatar(req.getAvatar());
			update.setAvatarUpdateTime(LocalDateTime.now());
		}

		userDao.updateById(update);
		// 删除缓存
		userCache.delete(uid);
		userSummaryCache.delete(uid);
		userSummaryCache.evictFriend(userSummaryCache.get(uid).getAccount());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void modifyAvatar(Long uid, ModifyAvatarReq req) {
        // 判断30天内是否改过头像
        User user = userDao.getById(uid);
//        AssertUtil.isTrue(Objects.isNull(user.getAvatarUpdateTime()) ||
//						user.getAvatarUpdateTime().plusDays(30).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() <=
//								LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
//				"30天内只能修改一次头像");


        // 更新
		User updateUser = User.builder().avatar(req.getAvatar()).avatarUpdateTime(LocalDateTime.now()).build();
		updateUser.setId(user.getId());
		userDao.updateById(updateUser);
        // 删除缓存
		userCache.delete(uid);
		userSummaryCache.delete(uid);
		userSummaryCache.evictFriend(user.getAccount());
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
		userCache.delete(uid);
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
    public List<ItemInfoDTO> getItemInfo(ItemInfoReq req) {//简单做，更新时间可判断被修改
		if(CollUtil.isEmpty(req.getReqList())){
			List<ItemConfig> allItems = itemCache.getAllItems();

			return allItems.stream().map(itemConfig -> {
				ItemInfoDTO dto = new ItemInfoDTO();
				dto.setItemId(itemConfig.getId());
				dto.setImg(itemConfig.getImg());
				dto.setDescribe(itemConfig.getDescribe());
				return dto;
			}).collect(Collectors.toList());
		}

        return req.getReqList().stream().map(a -> {
            ItemConfig itemConfig = itemCache.get(a.getItemId());
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
		SummeryInfoDTO userInfo = userSummaryCache.get(uid);
		User user = new User();
		user.setId(userInfo.getUid());
		user.setEmail(req.getEmail());
		boolean save = userDao.updateById(user);
		if(save){
			cachePlusOps.hDel("emailCode", req.getUuid());
			userCache.delete(uid);
			userSummaryCache.delete(uid);
		}
		return save;
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

    @Override
    @Transactional
    public Boolean register(UserRegisterVo userRegisterVo) {
        // 1. 检查邮箱是否已被其他用户绑定
		ContextUtil.setUid(DefValConstants.DEF_BOT_ID);
		ContextUtil.setTenantId(userRegisterVo.getTenantId());
        if (userDao.existsByEmailAndIdNot(null, userRegisterVo.getEmail())) {
			return false;
        }

        // 2. 直接使用 OAuth 服务传递过来的账号和用户名
        String account = userRegisterVo.getAccount();
        String userName = userRegisterVo.getName();

        log.info("用户注册，邮箱：{}，DefUser ID：{}，账号：{}，用户名：{}", userRegisterVo.getEmail(), userRegisterVo.getUserId(), account, userName);

        // 3. 创建 IM 用户
        User newUser = User.builder()
                .userId(userRegisterVo.getUserId())
                .avatar(userRegisterVo.getAvatar())
                .account(account)
                .email(userRegisterVo.getEmail())
                .sex(userRegisterVo.getSex())
                .userType(userRegisterVo.getUserType())
                .name(userName)
                .resume("这个人还没有填写个人简介呢")
                .openId(userRegisterVo.getOpenId())
                .tenantId(userRegisterVo.getTenantId())
                .context(false)
                .build();

        // 保存用户
        newUser.setCreateBy(1L);
        userDao.save(newUser);

		// 注入群组信息
		cachePlusOps.sAdd(PresenceCacheKeyBuilder.groupMembersKey(DefValConstants.DEF_ROOM_ID), newUser.getId());
		cachePlusOps.sAdd(PresenceCacheKeyBuilder.userGroupsKey(newUser.getId()), DefValConstants.DEF_ROOM_ID);

		// 加上系统机器人好友
		roomAppService.createSystemFriend(DefValConstants.DEF_ROOM_ID, DefValConstants.DEF_GROUP_ID, newUser.getId());

        // 发布用户注册消息
        SpringUtils.publishEvent(new UserRegisterEvent(this, newUser));
		return true;
    }
}
