package com.luohuo.flex.im.core.user.service.impl;

import com.luohuo.flex.im.core.user.dao.UserPrivacyDao;
import com.luohuo.flex.im.core.user.service.PrivacyService;
import com.luohuo.flex.im.domain.dto.PrivacySettingReq;
import com.luohuo.flex.im.domain.entity.UserPrivacy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrivacyServiceImpl implements PrivacyService {
    
    private final UserPrivacyDao userPrivacyDao;
    
    /**
     * 获取用户隐私设置
     */
    public UserPrivacy getPrivacySetting(Long uid) {
        UserPrivacy privacy = userPrivacyDao.getByUid(uid);
        if (privacy == null) {
            userPrivacyDao.initUserPrivacy(uid);
            return userPrivacyDao.getByUid(uid);
        }
        return privacy;
    }
    
    /**
     * 更新隐私设置
     */
    @Transactional
    public boolean updatePrivacySetting(Long uid, PrivacySettingReq req) {
        return userPrivacyDao.updatePrivacy(uid, req);
    }
    
    /**
     * 检查用户是否是私密账号
     */
    public boolean checkIsPrivate(Long uid) {
        Boolean isPrivate = userPrivacyDao.checkIsPrivate(uid);
        return isPrivate != null && isPrivate;
    }

	/**
	 * 检查用户是否允许临时会话
	 */
	public boolean checkAllowTempSession(Long uid) {
		// 从DAO层查询用户隐私设置中的allowTempSession字段
		Boolean allow = userPrivacyDao.checkAllowTempSession(uid);

		// 如果查询结果为null，默认返回true（允许临时会话）
		return allow == null || allow;
	}
}