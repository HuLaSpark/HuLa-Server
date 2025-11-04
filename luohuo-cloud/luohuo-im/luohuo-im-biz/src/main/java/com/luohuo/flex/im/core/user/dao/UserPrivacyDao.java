package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.core.user.mapper.UserPrivacyMapper;
import com.luohuo.flex.im.domain.dto.PrivacySettingReq;
import com.luohuo.flex.im.domain.entity.UserPrivacy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserPrivacyDao extends ServiceImpl<UserPrivacyMapper, UserPrivacy> {

	/**
	 * 获取用户隐私设置
	 */
	public UserPrivacy getByUid(Long uid) {
		return lambdaQuery().eq(UserPrivacy::getUid, uid).one();
	}

	/**
	 * 初始化用户隐私设置
	 */
	@Transactional
	public boolean initUserPrivacy(Long uid) {
		UserPrivacy privacy = new UserPrivacy();
		privacy.setUid(uid);
		privacy.setIsPrivate(false);
		privacy.setAllowTempSession(true);
		privacy.setSearchableByPhone(true);
		privacy.setSearchableByAccount(true);
		privacy.setSearchableByUsername(true);
		privacy.setShowOnlineStatus(true);
		privacy.setAllowAddFriend(true);
		privacy.setAllowGroupInvite(true);
		privacy.setHideProfile(false);
		return save(privacy);
	}

	/**
	 * 更新隐私设置
	 */
	@Transactional
	public boolean updatePrivacy(Long uid, PrivacySettingReq req) {
		return lambdaUpdate()
				.eq(UserPrivacy::getUid, uid)
				.set(req.getIsPrivate() != null, UserPrivacy::getIsPrivate, req.getIsPrivate())
				.set(req.getAllowTempSession() != null, UserPrivacy::getAllowTempSession, req.getAllowTempSession())
				.set(req.getSearchableByPhone() != null, UserPrivacy::getSearchableByPhone, req.getSearchableByPhone())
				.set(req.getSearchableByAccount() != null, UserPrivacy::getSearchableByAccount, req.getSearchableByAccount())
				.set(req.getSearchableByUsername() != null, UserPrivacy::getSearchableByUsername, req.getSearchableByUsername())
				.set(req.getShowOnlineStatus() != null, UserPrivacy::getShowOnlineStatus, req.getShowOnlineStatus())
				.set(req.getAllowAddFriend() != null, UserPrivacy::getAllowAddFriend, req.getAllowAddFriend())
				.set(req.getAllowGroupInvite() != null, UserPrivacy::getAllowGroupInvite, req.getAllowGroupInvite())
				.set(req.getHideProfile() != null, UserPrivacy::getHideProfile, req.getHideProfile())
				.update();
	}

	public Boolean checkAllowTempSession(Long uid) {
		return baseMapper.checkAllowTempSession(uid);
	}

	public Boolean checkIsPrivate(Long uid) {
		return baseMapper.checkIsPrivate(uid);
	}
}