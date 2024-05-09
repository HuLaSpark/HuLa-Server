package com.hula.core.user.controller;


import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.req.PageBaseReq;
import com.hula.common.domain.vo.resp.ApiResult;
import com.hula.common.domain.vo.resp.CursorPageBaseResp;
import com.hula.common.domain.vo.resp.PageBaseResp;
import com.hula.common.utils.RequestHolder;
import com.hula.core.user.domain.vo.req.friend.FriendApplyReq;
import com.hula.core.user.domain.vo.req.friend.FriendApproveReq;
import com.hula.core.user.domain.vo.req.friend.FriendCheckReq;
import com.hula.core.user.domain.vo.req.friend.FriendDeleteReq;
import com.hula.core.user.domain.vo.resp.friend.FriendApplyResp;
import com.hula.core.user.domain.vo.resp.friend.FriendCheckResp;
import com.hula.core.user.domain.vo.resp.friend.FriendResp;
import com.hula.core.user.domain.vo.resp.friend.FriendUnreadResp;
import com.hula.core.user.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 好友相关接口
 * @author nyh
 */
@RestController
@RequestMapping("/api/user/friend")
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
    public ApiResult<Void> apply(@Valid @RequestBody FriendApplyReq request) {
        Long uid = RequestHolder.get().getUid();
        friendService.apply(uid, request);
        return ApiResult.success();
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

    @GetMapping("/page")
    @Operation(summary = "联系人列表")
    public ApiResult<CursorPageBaseResp<FriendResp>> friendList(@Valid CursorPageBaseReq request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(friendService.friendList(uid, request));
    }
}

