package com.hula.core.chat.controller;

import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.req.IdReqVO;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.core.chat.domain.vo.request.ContactFriendReq;
import com.hula.core.chat.domain.vo.request.contact.ContactAddReq;
import com.hula.core.chat.domain.vo.request.contact.ContactHideReq;
import com.hula.core.chat.domain.vo.request.contact.ContactNotificationReq;
import com.hula.core.chat.domain.vo.request.contact.ContactShieldReq;
import com.hula.core.chat.domain.vo.request.contact.ContactTopReq;
import com.hula.core.chat.domain.vo.response.ChatRoomResp;
import com.hula.core.chat.service.RoomAppService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
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

	@PostMapping("createContact")
	@Operation(summary = "创建临时会话")
	public ApiResult<Boolean> createContact(@RequestBody @Valid ContactAddReq request) {
		Long uid = RequestHolder.get().getUid();
		return ApiResult.success(roomService.createContact(uid, request));
	}

    @GetMapping("/contact/page")
    @Operation(summary ="会话列表")
    public ApiResult<CursorPageBaseResp<ChatRoomResp>> getRoomPage(@Valid CursorPageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomService.getContactPage(request, uid));
    }

	@GetMapping("/contact/list")
	@Operation(summary ="会话列表")
	public ApiResult<List<ChatRoomResp>> getRoomList() {
		return ApiResult.success(roomService.getContactPage(RequestHolder.get().getUid()));
	}

    @GetMapping("/contact/detail")
    @Operation(summary ="会话详情")
    public ApiResult<ChatRoomResp> getContactDetail(@Valid IdReqVO request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomService.getContactDetail(uid, request.getId()));
    }

    @GetMapping("/contact/detail/friend")
    @Operation(summary ="会话详情(联系人列表发消息用)")
    public ApiResult<ChatRoomResp> getContactDetailByFriend(@Valid ContactFriendReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomService.getContactDetailByFriend(uid, request));
    }

	@PostMapping("setTop")
	@Operation(summary = "置顶会话")
	public ApiResult<Boolean> setTop(@RequestBody @Valid ContactTopReq request) {
		Long uid = RequestHolder.get().getUid();
		return ApiResult.success(roomService.setTop(uid, request));
	}

	@PostMapping("setHide")
	@Operation(summary = "隐藏/展示会话")
	public ApiResult<Boolean> setHide(@RequestBody @Valid ContactHideReq req) {
		Long uid = RequestHolder.get().getUid();
		return ApiResult.success(roomService.setHide(uid, req));
	}

	@PostMapping("notification")
	@Operation(summary = "免打扰")
	public ApiResult<Boolean> setNotification(@RequestBody @Valid ContactNotificationReq request) {
		return ApiResult.success(roomService.setNotification(RequestHolder.get().getUid(), request));
	}

	@PostMapping("setShield")
	@Operation(summary = "屏蔽/解除屏蔽")
	public ApiResult<Boolean> setShield(@RequestBody @Valid ContactShieldReq request) {
		Long uid = RequestHolder.get().getUid();
		return ApiResult.success(roomService.setShield(uid, request));
	}
}

