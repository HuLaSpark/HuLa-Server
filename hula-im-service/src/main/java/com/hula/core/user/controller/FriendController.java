package com.hula.core.user.controller;


import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.req.PageBaseReq;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.common.domain.vo.res.PageBaseResp;
import com.hula.core.chat.domain.vo.request.friend.FriendRemarkReq;
import com.hula.core.chat.domain.vo.response.ChatMemberListResp;
import com.hula.core.user.domain.entity.UserApply;
import com.hula.core.user.domain.vo.req.friend.FriendApplyReq;
import com.hula.core.user.domain.vo.req.friend.FriendApproveReq;
import com.hula.core.user.domain.vo.req.friend.FriendCheckReq;
import com.hula.core.user.domain.vo.req.friend.FriendDeleteReq;
import com.hula.core.user.domain.vo.req.friend.FriendReq;
import com.hula.core.user.domain.vo.resp.friend.FriendApplyResp;
import com.hula.core.user.domain.vo.resp.friend.FriendCheckResp;
import com.hula.core.user.domain.vo.resp.friend.FriendResp;
import com.hula.core.user.domain.vo.resp.friend.FriendUnreadResp;
import com.hula.core.user.service.FriendService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 好友相关接口
 * @author nyh
 */
@RestController
@RequestMapping("/user/friend")
@Tag(name = "好友相关接口")
@Slf4j
public class FriendController {
    @Resource
    private FriendService friendService;

    @GetMapping("/check")
    @Operation(summary = "批量判断是否是自己好友")
    public ApiResult<FriendCheckResp> check(@Valid FriendCheckReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.check(uid, request));
    }

    @PostMapping("/apply")
    @Operation(summary = "申请好友")
    public ApiResult<UserApply> apply(@Valid @RequestBody FriendApplyReq request) {
        return ApiResult.success(friendService.apply(RequestHolder.get().getUid(), request));
    }

	@GetMapping("search")
	@Operation(summary = "查找联系人")
	public ApiResult<List<ChatMemberListResp>> search(@Valid FriendReq friendReq) {
		return ApiResult.success(friendService.searchFriend(friendReq));
	}

	@DeleteMapping()
    @Operation(summary = "删除好友")
    public ApiResult<Void> delete(@Valid @RequestBody FriendDeleteReq request) {
        Long uid = RequestHolder.get().getUid();
        friendService.deleteFriend(uid, request.getTargetUid());
        return ApiResult.success();
    }

    @GetMapping("/apply/page")
    @Operation(summary = "好友申请列表")
    public ApiResult<PageBaseResp<FriendApplyResp>> page(@Valid PageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.pageApplyFriend(uid, request));
    }

    @GetMapping("/apply/unread")
    @Operation(summary = "申请未读数")
    public ApiResult<FriendUnreadResp> unread() {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.unread(uid));
    }

    @PutMapping("/apply")
    @Operation(summary = "审批同意")
    public ApiResult<Void> applyApprove(@Valid @RequestBody FriendApproveReq request) {
        friendService.applyApprove(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }

    @PutMapping("/reject")
    @Operation(summary = "审批拒绝")
    public ApiResult<Void> reject(@Valid @RequestBody FriendApproveReq request) {
        friendService.reject(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }

    @PutMapping("/ignore")
    @Operation(summary = "忽略审批")
    public ApiResult<Void> ignore(@Valid @RequestBody FriendApproveReq request) {
        friendService.ignore(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }

    @DeleteMapping("/deleteApprove")
    @Operation(summary = "删除好友申请")
    public ApiResult<Void> deleteApprove(@Valid @RequestBody FriendApproveReq request) {
        friendService.deleteApprove(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }

    @GetMapping("/page")
    @Operation(summary = "联系人列表")
    public ApiResult<CursorPageBaseResp<FriendResp>> friendList(@Valid CursorPageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.friendList(uid, request));
    }

	@PostMapping("/updateRemark")
	@Operation(summary = "修改好友备注")
	public ApiResult<Boolean> updateRemark(@Valid @RequestBody FriendRemarkReq request) {
		return ApiResult.success(friendService.updateRemark(RequestHolder.get().getUid(), request));
	}
}

