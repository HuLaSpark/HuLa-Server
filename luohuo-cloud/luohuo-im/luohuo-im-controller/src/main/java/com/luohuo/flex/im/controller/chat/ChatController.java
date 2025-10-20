package com.luohuo.flex.im.controller.chat;

import com.luohuo.basic.tenant.core.aop.TenantIgnore;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.vo.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.domain.dto.MsgReadInfoDTO;
import com.luohuo.flex.im.domain.vo.response.ChatMessageReadResp;
import com.luohuo.flex.model.entity.ws.ChatMessageResp;
import com.luohuo.flex.im.core.chat.service.ChatService;
import com.luohuo.flex.im.core.frequencyControl.annotation.FrequencyControl;
import com.luohuo.flex.im.domain.enums.BlackTypeEnum;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 群聊相关接口
 * @author nyh
 */
@RestController
@RequestMapping("/chat")
@Tag(name = "聊天室相关接口")
@Slf4j
public class ChatController {
    @Resource
    private ChatService chatService;
    @Resource
    private UserSummaryCache userSummaryCache;

    private Set<String> getBlackUidSet() {
        return userSummaryCache.getBlackMap().getOrDefault(BlackTypeEnum.UID.getType(), new HashSet<>());
    }

    @GetMapping("/msg/page")
    @Operation(summary ="消息列表")
//    @FrequencyControl(time = 120, count = 20, target = FrequencyControl.Target.IP)
    public R<CursorPageBaseResp<ChatMessageResp>> getMsgPage(@Valid ChatMessagePageReq request) {
        CursorPageBaseResp<ChatMessageResp> msgPage = chatService.getMsgPage(request, ContextUtil.getUid());
        filterBlackMsg(msgPage);
        return R.success(msgPage);
    }

	@PostMapping("/msg/list")
	@Operation(summary ="消息列表 [登录时消息同步用的]")
//    @FrequencyControl(time = 120, count = 20, target = FrequencyControl.Target.IP)
	public R<List<ChatMessageResp>> getMsgPage(@RequestBody MsgReq msgReq) {
		List<ChatMessageResp> msgPage = chatService.getMsgList(msgReq, ContextUtil.getUid());
		Set<String> blackMembers = getBlackUidSet();
		msgPage.removeIf(a -> blackMembers.contains(a.getFromUser().getUid().toString()));
		return R.success(msgPage);
	}

    private void filterBlackMsg(CursorPageBaseResp<ChatMessageResp> memberPage) {
        Set<String> blackMembers = getBlackUidSet();
        memberPage.getList().removeIf(a -> blackMembers.contains(a.getFromUser().getUid().toString()));
    }

    @PostMapping("/msg")
    @Operation(summary ="发送消息")
//    @FrequencyControl(target = FrequencyControl.Target.UID, time = 60, count = 10)
    public R<ChatMessageResp> sendMsg(@Valid @RequestBody ChatMessageReq request) {
        Long msgId = chatService.sendMsg(request, ContextUtil.getUid());
        // 返回完整消息格式，方便前端展示
        return R.success(chatService.getMsgResp(msgId, ContextUtil.getUid()));
    }

    @PutMapping("/msg/mark")
    @Operation(summary ="消息标记")
    public R<Void> setMsgMark(@Valid @RequestBody ChatMessageMarkReq request) {
        chatService.setMsgMark(ContextUtil.getUid(), request);
        return R.success();
    }

    @PutMapping("/msg/recall")
    @Operation(summary ="撤回消息")
    public R<Void> recallMsg(@Valid @RequestBody ChatMessageBaseReq request) {
        chatService.recallMsg(ContextUtil.getUid(), request);
        return R.success();
    }

    @GetMapping("/msg/read/page")
    @Operation(summary ="消息的已读未读列表")
    public R<CursorPageBaseResp<ChatMessageReadResp>> getReadPage(@Valid ChatMessageReadReq request) {
        Long uid = ContextUtil.getUid();
        return R.success(chatService.getReadPage(uid, request));
    }

    @GetMapping("/msg/read")
    @Operation(summary ="获取消息的已读未读总数")
    public R<Collection<MsgReadInfoDTO>> getReadInfo(@Valid ChatMessageReadInfoReq request) {
        Long uid = ContextUtil.getUid();
        return R.success(chatService.getMsgReadInfo(uid, request));
    }

    @PutMapping("/msg/read")
    @Operation(summary ="消息阅读上报")
	@TenantIgnore
    @FrequencyControl(target = FrequencyControl.Target.UID, time = 1, count = 5)
    public R<Void> msgRead(@Valid @RequestBody ChatMessageMemberReq request) {
        Long uid = ContextUtil.getUid();
        chatService.msgRead(uid, request);
        return R.success();
    }
}

