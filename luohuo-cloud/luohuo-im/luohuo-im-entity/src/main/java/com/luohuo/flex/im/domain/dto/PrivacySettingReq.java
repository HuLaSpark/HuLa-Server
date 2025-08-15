package com.luohuo.flex.im.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrivacySettingReq implements Serializable {
    
    // 是否私密账号
    private Boolean isPrivate;
    
    // 是否允许临时会话
    private Boolean allowTempSession;
    
    // 是否允许通过手机号搜索
    private Boolean searchableByPhone;
    
    // 是否允许通过账号搜索
    private Boolean searchableByAccount;
    
    // 是否允许通过用户名搜索
    private Boolean searchableByUsername;
    
    // 是否显示在线状态
    private Boolean showOnlineStatus;
    
    // 是否允许添加好友
    private Boolean allowAddFriend;
    
    // 是否允许群邀请
    private Boolean allowGroupInvite;
    
    // 是否隐藏个人资料
    private Boolean hideProfile;
}