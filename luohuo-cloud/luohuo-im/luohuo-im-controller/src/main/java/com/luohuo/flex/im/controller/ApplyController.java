package com.luohuo.flex.im.controller;

import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.core.user.service.ApplyService;
import com.luohuo.flex.im.domain.entity.UserApply;
import com.luohuo.flex.im.domain.vo.req.PageBaseReq;
import com.luohuo.flex.im.domain.vo.req.friend.FriendApplyReq;
import com.luohuo.flex.im.domain.vo.request.RoomApplyReq;
import com.luohuo.flex.im.domain.vo.request.member.ApplyReq;
import com.luohuo.flex.im.domain.vo.request.member.GroupApplyHandleReq;
import com.luohuo.flex.im.domain.vo.res.PageBaseResp;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendApplyResp;
import com.luohuo.flex.model.entity.ws.WSFriendApply;
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
 * 申请接口
 * @author 乾乾
 */
@RestController
@RequestMapping("/room/apply")
@Tag(name = "申请相关接口")
@Slf4j
public class ApplyController {
    @Resource
    private ApplyService applyService;

	@GetMapping("/page")
	@Operation(summary = "申请列表 [好友、群聊]")
	public R<PageBaseResp<FriendApplyResp>> page(@Valid PageBaseReq request) {
		Long uid = ContextUtil.getUid();
		return R.success(applyService.pageApplyFriend(uid, request));
	}

    @PostMapping("/apply")
    @Operation(summary = "好友申请")
    public R<UserApply> apply(@Valid @RequestBody FriendApplyReq request) {
        return R.success(applyService.handlerApply(ContextUtil.getUid(), request));
    }

    @GetMapping("/unread")
    @Operation(summary = "申请未读数")
    public R<WSFriendApply> unread() {
        Long uid = ContextUtil.getUid();
        return R.success(applyService.unread(uid));
    }

	@Operation(summary ="审批别人邀请的进群、好友申请")
	@PostMapping("/handler/apply")
	public R<Void> handlerApply(@Valid @RequestBody ApplyReq request) {
		applyService.handlerApply(ContextUtil.getUid(), request);
		return R.success();
	}

	@PostMapping("/group")
	@Operation(summary = "申请加群")
	public R<Boolean> applyGroup(@Valid @RequestBody RoomApplyReq request){
		return R.success(applyService.applyGroup(ContextUtil.getUid(), request));
	}

	@PostMapping("/adminHandleApply")
	@Operation(summary = "处理加群申请 [仅仅管理员、群主可调用]")
	public R<Void> handleApply(@Valid @RequestBody GroupApplyHandleReq request) {
		applyService.handleApply(ContextUtil.getUid(), request);
		return R.success();
	}

    @DeleteMapping("/delete")
    @Operation(summary = "删除好友申请")
    public R<Boolean> deleteApprove(@Valid @RequestBody ApplyReq request) {
		applyService.deleteApprove(ContextUtil.getUid(), request);
        return R.success();
    }
}

