package com.hula.core.user.domain.enums;

import com.hula.core.user.domain.vo.resp.ws.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *  ws前端请求类型枚举
 * @author nyh
 */
@AllArgsConstructor
@Getter
public enum WSRespTypeEnum {
    NO_INTERNET("noInternet", "无网络连接", WSLoginUrl.class),
    LOGIN_QR_CODE("loginQrCode", "登录二维码返回", WSLoginUrl.class),
    WAITING_AUTHORIZE("waitingAuthorize", "用户扫描成功等待授权", null),
    LOGIN_SUCCESS("loginSuccess", "用户登录成功返回用户信息", WSLoginSuccess.class),
    RECEIVE_MESSAGE("receiveMessage", "新消息", WSMessage.class),
    ONLINE("online", "上线通知", WSOnlineNotify.class),
	USER_STATE_CHANGE("userStateChange", "用户状态改变", null),
    TOKEN_EXPIRED("tokenExpired", "使前端的token失效，意味着前端需要重新登录", WsTokenExpire.class),
    INVALID_USER("invalidUser", "拉黑用户", WSBlack.class),
    MSG_MARK_ITEM("msgMarkItem", "消息标记", WSMsgMark.class),
    MSG_RECALL("msgRecall", "消息撤回", WSMsgRecall.class),
    REQUEST_NEW_FRIEND("requestNewFriend", "好友申请", WSFriendApply.class),
    NEW_FRIEND_SESSION("newFriendSession", "成员变动", WSMemberChange.class),
    OFFLINE("offline", "下线通知", WSOfflineNotify.class),
    REQUEST_APPROVAL_FRIEND("requestApprovalFriend", "同意好友请求", WSFriendApproval.class),
    ;

    private final String type;
    private final String desc;
    private final Class<?> dataClass;

    private static Map<String, WSRespTypeEnum> cache;

    static {
        cache = Arrays.stream(WSRespTypeEnum.values()).collect(Collectors.toMap(WSRespTypeEnum::getType, Function.identity()));
    }

    public static WSRespTypeEnum of(String type) {
        return cache.get(type);
    }
}
