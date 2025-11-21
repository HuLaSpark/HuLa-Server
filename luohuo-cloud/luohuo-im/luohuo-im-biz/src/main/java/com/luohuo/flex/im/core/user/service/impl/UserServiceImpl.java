package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.luohuo.flex.im.domain.vo.req.PageBaseReq;
import com.luohuo.flex.im.domain.vo.req.user.*;
import com.luohuo.flex.im.domain.vo.res.PageBaseResp;
import com.luohuo.flex.im.domain.vo.resp.user.BlackPageResp;
import com.luohuo.flex.im.domain.vo.resp.user.UserSearchResp;
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
import com.luohuo.flex.im.domain.vo.resp.user.BadgeResp;
import com.luohuo.flex.im.domain.vo.resp.user.UserInfoResp;
import com.luohuo.flex.im.core.user.service.UserService;
import com.luohuo.flex.im.core.user.service.adapter.UserAdapter;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Transactional(rollbackFor = Exception.class)
    public void addBlack(BlackAddReq req) {
        // 去掉前后空格
        String target = StrUtil.trim(req.getTarget());

        // 先查询是否已经存在该黑名单记录
        LambdaQueryWrapper<Black> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Black::getType, req.getType())
               .eq(Black::getTarget, target);
        Black existingBlack = blackDao.getOne(wrapper);

        LocalDateTime deadline;
        if (ObjectUtil.isNull(req.getDeadline()) || req.getDeadline().equals(0L)) {
            deadline = MAX_DATE;
        } else {
            deadline = LocalDateTime.now().plusMinutes(req.getDeadline());
        }

        if (existingBlack != null) {
            existingBlack.setDeadline(deadline);
            blackDao.updateById(existingBlack);
        } else {
            Black black = new Black();
            black.setType(req.getType());
            black.setTarget(target);
            black.setDeadline(deadline);
            blackDao.save(black);
        }

        // 如果是拉黑用户，同时拉黑其IP
        if (BlackTypeEnum.UID.getType().equals(req.getType())) {
            try {
                Long uid = Long.parseLong(target);
                User user = userDao.getById(uid);
                if (user != null) {
                    blackIp(user.getIpInfo().getCreateIp(), deadline);
                    blackIp(user.getIpInfo().getUpdateIp(), deadline);
                    SpringUtils.publishEvent(new UserBlackEvent(this, user));
                }
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        }

        // 清除黑名单缓存
        userSummaryCache.evictBlackMap();
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

    /**
     * 拉黑IP
     * @param ip IP地址
     * @param deadline 截止时间
     */
    public void blackIp(String ip, LocalDateTime deadline) {
        if (StrUtil.isBlank(ip)) {
            return;
        }

        // 先查询是否已经存在该IP黑名单
        LambdaQueryWrapper<Black> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Black::getType, BlackTypeEnum.IP.getType())
               .eq(Black::getTarget, ip);
        Black existingBlack = blackDao.getOne(wrapper);

        if (existingBlack != null) {
            // 已存在，更新截止时间
            existingBlack.setDeadline(deadline);
            blackDao.updateById(existingBlack);
        } else {
            // 不存在，新增
            Black black = new Black();
            black.setTarget(ip);
            black.setType(BlackTypeEnum.IP.getType());
            black.setDeadline(deadline);
            blackDao.save(black);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editBlack(BlackEditReq req) {
        Black black = blackDao.getById(req.getId());
        AssertUtil.isNotEmpty(black, "黑名单记录不存在");

        black.setType(req.getType());
        black.setTarget(req.getTarget());

        if (StrUtil.isBlank(req.getDeadline())) {
            black.setDeadline(MAX_DATE);
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                black.setDeadline(LocalDateTime.parse(req.getDeadline(), formatter));
            } catch (Exception e) {
                throw new BizException("截止时间格式错误，请使用 yyyy-MM-dd HH:mm:ss 格式");
            }
        }
        blackDao.updateById(black);
        // 清除黑名单缓存
        userSummaryCache.evictBlackMap();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeBlack(BlackRemoveReq req) {
        Black black = blackDao.getById(req.getId());
        AssertUtil.isNotEmpty(black, "黑名单记录不存在");
        blackDao.removeById(req.getId());
        // 清除黑名单缓存
        userSummaryCache.evictBlackMap();
    }

    @Override
    public PageBaseResp<BlackPageResp> blackPage(BlackPageReq req) {
        // 去掉前后空格
        String searchKeyword = StrUtil.trim(req.getTarget());

        LambdaQueryWrapper<Black> wrapper = new LambdaQueryWrapper<>();

        // 如果提供了搜索关键词，需要按用户昵称或IP模糊查询
        if (StrUtil.isNotBlank(searchKeyword)) {
            // 先查询所有黑名单记录
            wrapper.orderByDesc(Black::getCreateTime);
            Page<Black> allPage = blackDao.page(req.plusPage(), wrapper);

            // 过滤：IP类型按IP模糊匹配，UID类型按用户昵称模糊匹配
            List<BlackPageResp> filteredList = allPage.getRecords().stream()
                    .map(black -> {
                        BlackPageResp resp = BlackPageResp.builder()
                                .id(black.getId())
                                .type(black.getType())
                                .target(black.getTarget())
                                .deadline(black.getDeadline())
                                .createTime(black.getCreateTime())
                                .build();

                        // 如果是UID类型，查询用户昵称
                        if (BlackTypeEnum.UID.getType().equals(black.getType())) {
                            try {
                                Long uid = Long.parseLong(black.getTarget());
                                User user = userDao.getById(uid);
                                if (user != null) {
                                    resp.setUserName(user.getName());
                                }
                            } catch (NumberFormatException e) {
                                // 忽略解析错误
                            }
                        }

                        return resp;
                    })
                    .filter(resp -> {
                        // IP类型：按IP模糊匹配
                        if (BlackTypeEnum.IP.getType().equals(resp.getType())) {
                            return StrUtil.contains(resp.getTarget(), searchKeyword);
                        }
                        // UID类型：按用户昵称模糊匹配
                        else if (BlackTypeEnum.UID.getType().equals(resp.getType())) {
                            return StrUtil.isNotBlank(resp.getUserName()) &&
                                   StrUtil.contains(resp.getUserName(), searchKeyword);
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            return PageBaseResp.init((int) allPage.getCurrent(), (int) allPage.getSize(), (long) filteredList.size(), filteredList);
        } else {
            // 没有搜索关键词，查询所有记录
            wrapper.orderByDesc(Black::getCreateTime);
            Page<Black> page = blackDao.page(req.plusPage(), wrapper);

            List<BlackPageResp> list = page.getRecords().stream()
                    .map(black -> {
                        BlackPageResp resp = BlackPageResp.builder()
                                .id(black.getId())
                                .type(black.getType())
                                .target(black.getTarget())
                                .deadline(black.getDeadline())
                                .createTime(black.getCreateTime())
                                .build();

                        // 如果是UID类型，查询用户昵称
                        if (BlackTypeEnum.UID.getType().equals(black.getType())) {
                            try {
                                Long uid = Long.parseLong(black.getTarget());
                                User user = userDao.getById(uid);
                                if (user != null) {
                                    resp.setUserName(user.getName());
                                }
                            } catch (NumberFormatException e) {
                                // 忽略解析错误
                            }
                        }

                        return resp;
                    })
                    .collect(Collectors.toList());

            return PageBaseResp.init((int) page.getCurrent(), (int) page.getSize(), page.getTotal(), list);
        }
    }

    @Override
    public PageBaseResp<UserSearchResp> searchUser(UserSearchReq req) {
        Page<User> page = new Page<>(req.getPageNo(), req.getPageSize());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 如果提供了id，优先按id精确查询（用于回显）
        if (ObjectUtil.isNotNull(req.getId())) {
			wrapper.eq(User::getId, req.getId());
        } else if (StrUtil.isNotBlank(req.getKeyword())) {
            wrapper.like(User::getName, req.getKeyword());
        }

        wrapper.orderByDesc(User::getLastOptTime);

        IPage<User> userPage = userDao.page(page, wrapper);
        List<UserSearchResp> list = userPage.getRecords().stream()
                .map(user -> UserSearchResp.builder()
                        .uid(String.valueOf(user.getId()))
                        .name(user.getName())
                        .avatar(user.getAvatar())
                        .account(user.getAccount())
                        .build())
                .collect(Collectors.toList());

        return PageBaseResp.init((int) userPage.getCurrent(), (int) userPage.getSize(), userPage.getTotal(), list);
    }
}
