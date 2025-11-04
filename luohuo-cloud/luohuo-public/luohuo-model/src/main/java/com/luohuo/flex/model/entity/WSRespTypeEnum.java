package com.luohuo.flex.model.entity;

import com.luohuo.flex.model.entity.ws.*;
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
	ROOM_INFO_CHANGE("roomInfoChange", "管理员修改群聊信息", ChatRoomGroupChange.class),
	MY_ROOM_INFO_CHANGE("myRoomInfoChange", "自己修改我在群里的信息", ChatMyRoomGroupChange.class),
    TOKEN_EXPIRED("tokenExpired", "使前端的token失效，意味着前端需要重新登录", OffLineResp.class),
    INVALID_USER("invalidUser", "拉黑用户", WSBlack.class),
    MSG_MARK_ITEM("msgMarkItem", "消息标记", WSMsgMark.class),
    MSG_RECALL("msgRecall", "消息撤回", WSMsgRecall.class),
	DELETE_FRIEND("deleteFriend", "删除好友", null),

	NOTIFY_EVENT("notifyEvent", "通知总线", null),
	REQUEST_APPROVAL_FRIEND("requestApprovalFriend", "同意好友请求", WSFriendApproval.class),
	NEW_APPLY("newApply", "好友申请、群聊邀请", WSNotice.class),
	ROOM_DISSOLUTION("roomDissolution", "群解散", null),
	GROUP_SET_ADMIN("groupSetAdmin", "设置管理员", AdminChangeDTO.class),

	ROOM_GROUP_NOTICE_READ_MSG("roomGroupNoticeReadMsg", "群公告已读", null),
	FEED_SEND_MSG("feedSendMsg", "朋友圈发布", null),
	FEED_NOTIFY("feedNotify", "朋友圈通知（点赞/评论）", null),
	ROOM_NOTIFICATION("roomNotification", "会话消息接收类型改变", null),
	SHIELD("shield", "你已屏蔽好友的消息", null),
	UNBLOCK("unblock", "你已解除屏蔽好友的消息", null),
	memberChange("memberChange", "成员变动", WSMemberChange.class),
    OFFLINE("offline", "下线通知", WSOnlineNotify.class),
	WSReconnect("WSReconnect", "ws消息重连", null),
	JoinVideo("JoinVideo", "加入视频会议", null),
	VideoCallRequest("VideoCallRequest","发起通话请求", null),
	StartSignaling("StartSignaling","开始呼叫", null),
	CallAccepted("CallAccepted","通话已接通", null),
	CallRejected("CallRejected","呼叫被拒绝", null),
	RoomClosed("RoomClosed","会议已关闭", null),
	MediaControl("MediaControl","媒体组件改变", null),
	TIMEOUT("TIMEOUT","通话超时", null),
	CANCEL("CANCEL", "取消通话", null),
	DROPPED("DROPPED", "挂断通话", null),
	WEBRTC_SIGNAL("WEBRTC_SIGNAL", "信令消息", null),
	LeaveVideo("LeaveVideo", "离开视频会议", null),
	ScreenSharingStarted("ScreenSharingStarted", "启动屏幕共享", null),
	ScreenSharingStopped("ScreenSharingStopped", "关闭屏幕共享", null),
	NetworkPoor("NetworkPoor", "网络状况不佳", null),
	UserKicked("UserKicked", "踢出用户", null),
	AllMuted("AllMuted", "全局静音", null)
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
