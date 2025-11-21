package com.luohuo.flex.im.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.domain.vo.request.friend.FriendPermissionReq;
import com.luohuo.flex.im.domain.vo.request.friend.FriendRemarkReq;
import com.luohuo.flex.im.domain.vo.response.ChatMemberListResp;
import com.luohuo.flex.im.domain.vo.req.friend.FriendCheckReq;
import com.luohuo.flex.im.domain.vo.req.friend.FriendDeleteReq;
import com.luohuo.flex.im.domain.vo.req.friend.FriendReq;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendCheckResp;
import com.luohuo.flex.im.domain.vo.resp.friend.FriendResp;
import com.luohuo.flex.im.core.user.service.FriendService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 好友相关接口
 * @author 乾乾
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
    public R<FriendCheckResp> check(@Valid FriendCheckReq request) {
        Long uid = ContextUtil.getUid();
        return R.success(friendService.check(uid, request));
    }

	@GetMapping("search")
	@Operation(summary = "查找联系人")
	public R<List<ChatMemberListResp>> search(@Valid FriendReq friendReq) {
		return R.success(friendService.searchFriend(friendReq));
	}

	@DeleteMapping
    @Operation(summary = "删除好友")
    public R<Boolean> delete(@Valid @RequestBody FriendDeleteReq request) {
        Long uid = ContextUtil.getUid();
        friendService.deleteFriend(uid, request.getTargetUid());
        return R.success();
    }

    @GetMapping("/page")
    @Operation(summary = "联系人列表")
    public R<CursorPageBaseResp<FriendResp>> friendList(@Valid CursorPageBaseReq request, @RequestParam(required = false) Long uid) {
        // 如果没有传入uid，则使用当前登录用户的uid（普通用户场景）
        // 如果传入了uid，则查询指定用户的好友列表（管理员场景）
        Long targetUid = uid != null ? uid : ContextUtil.getUid();
        return R.success(friendService.friendList(targetUid, request));
    }

	@PostMapping("/updateRemark")
	@Operation(summary = "修改好友备注")
	public R<Boolean> updateRemark(@Valid @RequestBody FriendRemarkReq request) {
		return R.success(friendService.updateRemark(ContextUtil.getUid(), request));
	}

	@PostMapping("/permissionSettings")
	@Operation(summary = "好友权限设置")
	public R<Boolean> permissionSettings(@RequestBody FriendPermissionReq request) {
		return R.success(friendService.permissionSettings(ContextUtil.getUid(), request));
	}
}

