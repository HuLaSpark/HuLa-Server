package com.luohuo.flex.im.controller.chat;

import com.luohuo.flex.im.domain.vo.req.IdReqVO;
import com.luohuo.flex.im.domain.vo.request.contact.ContactAddReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.domain.vo.request.ContactFriendReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactHideReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactNotificationReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactShieldReq;
import com.luohuo.flex.im.domain.vo.request.contact.ContactTopReq;
import com.luohuo.flex.im.domain.vo.response.ChatRoomResp;
import com.luohuo.flex.im.core.chat.service.RoomAppService;

import java.util.List;

/**
 * 会话相关接口
 * @author nyh
 */
@RestController
@RequestMapping("/chat")
@Tag(name = "聊天室相关接口")
@Slf4j
public class ContactController {
    @Resource
    private RoomAppService roomService;

	@PostMapping("/contact/create")
	@Operation(summary = "创建临时会话")
	public R<Boolean> createContact(@RequestBody @Valid ContactAddReq request) {
		Long uid = ContextUtil.getUid();
		return R.success(roomService.createContact(uid, request));
	}

    @GetMapping("/contact/page")
    @Operation(summary ="会话列表")
    public R<CursorPageBaseResp<ChatRoomResp>> getRoomPage(@Valid CursorPageBaseReq request) {
        Long uid = ContextUtil.getUid();
        return R.success(roomService.getContactPage(request, uid));
    }

	@GetMapping("/contact/list")
	@Operation(summary ="会话列表")
	public R<List<ChatRoomResp>> getRoomList() {
		return R.success(roomService.getContactPage(ContextUtil.getUid()));
	}

    @GetMapping("/contact/detail")
    @Operation(summary ="会话详情")
    public R<ChatRoomResp> getContactDetail(@Valid IdReqVO request) {
        Long uid = ContextUtil.getUid();
        return R.success(roomService.getContactDetail(uid, request.getId()));
    }

    @GetMapping("/contact/detail/friend")
    @Operation(summary ="会话详情(联系人列表发消息用)")
    public R<ChatRoomResp> getContactDetailByFriend(@Valid ContactFriendReq request) {
		Long uid = ContextUtil.getUid();
        return R.success(roomService.getContactDetailByFriend(uid, request));
    }

	@PostMapping("setTop")
	@Operation(summary = "置顶会话")
	public R<Boolean> setTop(@RequestBody @Valid ContactTopReq request) {
		Long uid = ContextUtil.getUid();
		return R.success(roomService.setTop(uid, request));
	}

	@PostMapping("setHide")
	@Operation(summary = "隐藏/展示会话")
	public R<Boolean> setHide(@RequestBody @Valid ContactHideReq req) {
		Long uid = ContextUtil.getUid();
		return R.success(roomService.setHide(uid, req));
	}

	@PostMapping("notification")
	@Operation(summary = "免打扰")
	public R<Boolean> setNotification(@RequestBody @Valid ContactNotificationReq request) {
		return R.success(roomService.setNotification(ContextUtil.getUid(), request));
	}

	@PostMapping("setShield")
	@Operation(summary = "屏蔽/解除屏蔽")
	public R<Boolean> setShield(@RequestBody @Valid ContactShieldReq request) {
		Long uid = ContextUtil.getUid();
		return R.success(roomService.setShield(uid, request));
	}
}

