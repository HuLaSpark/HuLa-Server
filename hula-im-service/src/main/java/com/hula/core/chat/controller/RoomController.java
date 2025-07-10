package com.hula.core.chat.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hula.common.domain.vo.req.IdReqVO;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.common.domain.vo.res.GroupListVO;
import com.hula.common.domain.vo.res.IdRespVO;
import com.hula.core.chat.domain.entity.Announcements;
import com.hula.core.chat.domain.entity.RoomGroup;
import com.hula.core.chat.domain.vo.request.ChatMessageMemberReq;
import com.hula.core.chat.domain.vo.request.GroupAddReq;
import com.hula.core.chat.domain.vo.request.RoomApplyReq;
import com.hula.core.chat.domain.vo.request.RoomInfoReq;
import com.hula.core.chat.domain.vo.request.RoomMyInfoReq;
import com.hula.core.chat.domain.vo.request.admin.AdminAddReq;
import com.hula.core.chat.domain.vo.request.admin.AdminRevokeReq;
import com.hula.core.chat.domain.vo.request.member.MemberAddReq;
import com.hula.core.chat.domain.vo.request.member.MemberDelReq;
import com.hula.core.chat.domain.vo.request.member.MemberExitReq;
import com.hula.core.chat.domain.vo.request.member.MemberReq;
import com.hula.core.chat.domain.vo.request.room.AnnouncementsParam;
import com.hula.core.chat.domain.vo.request.room.ReadAnnouncementsParam;
import com.hula.core.chat.domain.vo.request.room.RoomGroupReq;
import com.hula.core.chat.domain.vo.response.ChatMemberListResp;
import com.hula.core.chat.domain.vo.response.ChatMessageResp;
import com.hula.core.chat.domain.vo.response.MemberResp;
import com.hula.core.chat.service.IGroupMemberService;
import com.hula.core.chat.service.RoomAppService;
import com.hula.core.user.domain.vo.req.MergeMessageReq;
import com.hula.core.user.domain.vo.resp.ws.ChatMemberResp;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房间相关接口
 * @author nyh
 */
@RestController
@RequestMapping("/room")
@Tag(name = "聊天室相关接口")
@Slf4j
public class RoomController {
    @Resource
    private RoomAppService roomService;
    @Resource
    private IGroupMemberService groupMemberService;

    @GetMapping("/group")
    @Operation(summary ="群组详情")
    public ApiResult<MemberResp> groupDetail(@Valid IdReqVO request) {
        Long uid = RequestHolder.get().getUid();
        return ApiResult.success(roomService.getGroupDetail(uid, request.getId()));
    }

	@GetMapping("search")
	@Operation(summary = "查找群聊")
	public ApiResult<List<RoomGroup>> searchGroup(@Valid RoomGroupReq req) {
		return ApiResult.success(roomService.searchGroup(req));
	}

    /**
     * 群聊列表
     *
     * @param current 当前页
     * @param size    大小
     * @return {@link ApiResult }
     */
    @GetMapping("/group/list")
    public ApiResult<IPage<GroupListVO>> groupList(@RequestParam("current") Long current,@RequestParam("size") Long size){
        Long uid = RequestHolder.get().getUid();
        IPage<GroupListVO>  page = new Page<>(current,size);
        return ApiResult.success(roomService.groupList(uid,page));
    }

    @GetMapping("/group/member/page")
    @Operation(summary ="群成员列表")
    public ApiResult<CursorPageBaseResp<ChatMemberResp>> getMemberPage(@Valid MemberReq request) {
        return ApiResult.success(roomService.getMemberPage(request));
    }

    @GetMapping("/group/listMember")
    @Operation(summary ="群成员列表")
    public ApiResult<List<ChatMemberResp>> listMember(@Valid MemberReq request) {
        List<ChatMemberResp> list = roomService.listMember(request);
        return ApiResult.success(list);
    }

    @GetMapping("/group/member/list")
    @Operation(summary ="房间内的所有群成员列表-@专用")
    public ApiResult<List<ChatMemberListResp>> getMemberList(@Valid ChatMessageMemberReq request) {
        List<ChatMemberListResp> memberList = roomService.getMemberList(request);
        return ApiResult.success(memberList);
    }

    @DeleteMapping("/group/member")
    @Operation(summary ="移除成员")
    public ApiResult<Void> delMember(@Valid @RequestBody MemberDelReq request) {
        Long uid = RequestHolder.get().getUid();
        roomService.delMember(uid, request);
        return ApiResult.success();
    }

    @DeleteMapping("/group/member/exit")
    @Operation(summary ="退出群聊 | 解散群聊")
    public ApiResult<Boolean> exitGroup(@Valid @RequestBody MemberExitReq request) {
        groupMemberService.exitGroup(RequestHolder.get().getUid(), request);
        return ApiResult.success();
    }

    @PostMapping("/group")
    @Operation(summary ="新增群组")
    public ApiResult<IdRespVO> addGroup(@Valid @RequestBody GroupAddReq request) {
        Long uid = RequestHolder.get().getUid();
        Long roomId = roomService.addGroup(uid, request);
        return ApiResult.success(IdRespVO.builder().id(roomId).build());
    }

	@PostMapping("updateRoomInfo")
	@Operation(summary = "修改群信息")
	public ApiResult<Boolean> updateRoomInfo(@Valid @RequestBody RoomInfoReq request) {
		Long uid = RequestHolder.get().getUid();
		return ApiResult.success(roomService.updateRoomInfo(uid, request));
	}

	@PostMapping("updateMyRoomInfo")
	@Operation(summary = "修改'我的'群信息")
	public ApiResult<Boolean> updateMyRoomInfo(@Valid @RequestBody RoomMyInfoReq request) {
		Long uid = RequestHolder.get().getUid();
		return ApiResult.success(roomService.updateMyRoomInfo(uid, request));
	}

	@PostMapping("/applyGroup")
	@Operation(summary = "申请加群")
	public ApiResult<Boolean> applyGroup(@Valid @RequestBody RoomApplyReq request){
		return ApiResult.success(roomService.applyGroup(RequestHolder.get().getUid(), request));
	}

    @PostMapping("/group/member")
    @Operation(summary ="邀请好友")
    public ApiResult<Void> addMember(@Valid @RequestBody MemberAddReq request) {
        Long uid = RequestHolder.get().getUid();
        roomService.addMember(uid, request);
        return ApiResult.success();
    }

    @PutMapping("/group/admin")
    @Operation(summary ="添加管理员")
    public ApiResult<Boolean> addAdmin(@Valid @RequestBody AdminAddReq request) {
        Long uid = RequestHolder.get().getUid();
        groupMemberService.addAdmin(uid, request);
        return ApiResult.success();
    }

    @DeleteMapping("/group/admin")
    @Operation(summary ="撤销管理员")
    public ApiResult<Boolean> revokeAdmin(@Valid @RequestBody AdminRevokeReq request) {
        Long uid = RequestHolder.get().getUid();
        groupMemberService.revokeAdmin(uid, request);
        return ApiResult.success();
    }

	@Operation(summary = "公告列表")
	@GetMapping("/announcement/list/{id}")
	public ApiResult<IPage<Announcements>> announcementList(@PathVariable("id") Long roomId, @RequestParam("current") Long current,@RequestParam("size") Long size){
		IPage<Announcements> page = new Page<>(current,size);
		return ApiResult.success(roomService.announcementList(roomId, page));
	}

	@Operation(summary = "查看公告")
	@GetMapping("/announcement")
	public ApiResult announcement(ReadAnnouncementsParam param){
		return ApiResult.success(roomService.getAnnouncement(RequestHolder.get().getUid(), param));
	}

	@Operation(summary = "发布公告")
	@PostMapping("announcement/push")
	public ApiResult pushAnnouncement(@Valid @RequestBody AnnouncementsParam param){
		return ApiResult.success(roomService.pushAnnouncement(RequestHolder.get().getUid(), param));
	}

	@Operation(summary = "编辑公告/顶置公告")
	@PostMapping("announcement/edit")
	public ApiResult announcementEdit(@Valid @RequestBody AnnouncementsParam param){
		return ApiResult.returnResult("编辑", roomService.announcementEdit(RequestHolder.get().getUid(), param));
	}

	@Operation(summary = "删除公告")
	@PostMapping("announcement/delete/{id}")
	public ApiResult announcementDelete(@PathVariable("id") Long id){
		return ApiResult.success(roomService.announcementDelete(RequestHolder.get().getUid(), id));
	}

	@Operation(summary = "已读公告")
	@PostMapping("announcement/read")
	public ApiResult readAnnouncement(@RequestBody ReadAnnouncementsParam param){
		return ApiResult.success(roomService.readAnnouncement(RequestHolder.get().getUid(), param));
	}

	@Operation(summary = "合并消息")
	@PostMapping("mergeMessage")
	public ApiResult<ChatMessageResp> mergeMessage(@Validated @RequestBody MergeMessageReq req){
		return ApiResult.success(roomService.mergeMessage(RequestHolder.get().getUid(), req));
	}
}
