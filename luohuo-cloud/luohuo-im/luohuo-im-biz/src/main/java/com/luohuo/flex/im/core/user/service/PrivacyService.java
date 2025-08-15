package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.domain.dto.PrivacySettingReq;
import com.luohuo.flex.im.domain.entity.UserPrivacy;

public interface PrivacyService {
    
    /**
     * 获取用户隐私设置
     */
	UserPrivacy getPrivacySetting(Long uid);
    
    /**
     * 更新隐私设置
     */
	boolean updatePrivacySetting(Long uid, PrivacySettingReq req);
    
    /**
     * 检查用户是否允许临时会话
     */
	boolean checkAllowTempSession(Long uid);
    
    /**
     * 检查用户是否是私密账号
     */
	boolean checkIsPrivate(Long uid);
}