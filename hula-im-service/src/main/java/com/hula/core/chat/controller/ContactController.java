package com.hula.core.chat.controller;

import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.req.IdReqVO;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.core.chat.domain.vo.request.ContactFriendReq;
import com.hula.core.chat.domain.vo.request.contact.ContactNotificationReq;
import com.hula.core.chat.domain.vo.request.contact.ContactTopReq;
import com.hula.core.chat.domain.vo.response.ChatRoomResp;
import com.hula.core.chat.service.ChatService;
import com.hula.core.chat.service.RoomAppService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private ChatService chatService;
    @Resource
    private RoomAppService roomService;

    @GetMapping("/contact/page")
    @Operation(summary ="会话列表")
    public ApiResult<CursorPageBaseResp<ChatRoomResp>> getRoomPage(@Valid CursorPageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomService.getContactPage(request, uid));
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

	@DeleteMapping("delete")
	@Operation(summary = "删除会话")
	public ApiResult<Boolean> delete(@RequestBody @Valid IdReqVO request) {
		Long uid = RequestHolder.get().getUid();
		return ApiResult.success(roomService.delContact(uid, request.getId()));
	}

	@PostMapping("notification")
	@Operation(summary = "免打扰")
	public ApiResult<Boolean> setNotification(@RequestBody @Valid ContactNotificationReq request) {
		return ApiResult.success(roomService.setNotification(RequestHolder.get().getUid(), request));
	}
}

