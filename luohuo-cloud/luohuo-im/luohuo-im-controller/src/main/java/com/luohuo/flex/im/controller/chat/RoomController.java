package com.luohuo.flex.im.controller.chat;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luohuo.flex.im.domain.vo.req.IdReqVO;
import com.luohuo.flex.im.domain.vo.req.room.DisbandGroupReq;
import com.luohuo.flex.im.domain.vo.req.room.GroupMemberPageReq;
import com.luohuo.flex.im.domain.vo.req.room.GroupPageReq;
import com.luohuo.flex.im.domain.vo.req.room.UpdateMemberNicknameReq;
import com.luohuo.flex.im.domain.vo.res.IdRespVO;
import com.luohuo.flex.im.domain.vo.res.PageBaseResp;
import com.luohuo.flex.im.domain.vo.resp.room.GroupMemberSimpleResp;
import com.luohuo.flex.im.domain.vo.response.GroupResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.domain.entity.Announcements;
import com.luohuo.flex.im.domain.entity.RoomGroup;
import com.luohuo.flex.im.domain.vo.request.ChatMessageMemberReq;
import com.luohuo.flex.im.domain.vo.request.GroupAddReq;
import com.luohuo.flex.im.domain.vo.request.RoomInfoReq;
import com.luohuo.flex.im.domain.vo.request.RoomMyInfoReq;
import com.luohuo.flex.im.domain.vo.request.admin.AdminSetReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberAddReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberDelReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberExitReq;
import com.luohuo.flex.im.domain.vo.request.member.MemberReq;
import com.luohuo.flex.im.domain.vo.request.room.AnnouncementsParam;
import com.luohuo.flex.im.domain.vo.request.room.ReadAnnouncementsParam;
import com.luohuo.flex.im.domain.vo.request.room.RoomGroupReq;
import com.luohuo.flex.im.domain.vo.response.ChatMemberListResp;
import com.luohuo.flex.im.domain.vo.response.MemberResp;
import com.luohuo.flex.im.core.chat.service.RoomAppService;
import com.luohuo.flex.im.domain.vo.req.MergeMessageReq;
import com.luohuo.flex.model.entity.ws.ChatMemberResp;

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

    @GetMapping("/group/detail")
    @Operation(summary ="群组详情")
    public R<MemberResp> groupDetail(@Valid IdReqVO request) {
        Long uid = ContextUtil.getUid();
        return R.success(roomService.getGroupDetail(uid, request.getId()));
    }

	@GetMapping("/group/info")
	@Operation(summary ="群组基础信息[没加群的人也可以查]")
	public R<GroupResp> groupInfo(@Valid IdReqVO request) {
		Long uid = ContextUtil.getUid();
		return R.success(roomService.getGroupInfo(uid, request.getId()));
	}

	@GetMapping("search")
	@Operation(summary = "查找群聊")
	public R<List<RoomGroup>> searchGroup(@Valid RoomGroupReq req) {
		return R.success(roomService.searchGroup(req));
	}

	@Operation(summary = "群聊列表")
    @GetMapping("/group/list")
    public R<List<MemberResp>> groupList(){
        Long uid = ContextUtil.getUid();
        return R.success(roomService.groupList(uid));
    }

	@GetMapping("/group/listMember")
	@Operation(summary ="群成员列表")
	public R<List<ChatMemberResp>> listMember(@Valid MemberReq request) {
		return R.success(roomService.listMember(request));
	}

    @GetMapping("/group/member/list")
    @Operation(summary ="房间内的所有群成员列表-@专用")
    public R<List<ChatMemberListResp>> getMemberList(@Valid ChatMessageMemberReq request) {
        List<ChatMemberListResp> memberList = roomService.getMemberList(request);
        return R.success(memberList);
    }

    @DeleteMapping("/group/member")
    @Operation(summary ="移除群成员")
    public R<Void> delMember(@Valid @RequestBody MemberDelReq request) {
        Long uid = ContextUtil.getUid();
        roomService.delMember(uid, request);
        return R.success();
    }

    @DeleteMapping("/group/member/exit")
    @Operation(summary ="退出群聊 | 解散群聊")
    public R<Boolean> exitGroup(@Valid @RequestBody MemberExitReq request) {
		roomService.exitGroup(false, ContextUtil.getUid(), request);
        return R.success();
    }

    @PostMapping("/group")
    @Operation(summary ="新增群组")
    public R<IdRespVO> addGroup(@Valid @RequestBody GroupAddReq request) {
        Long uid = ContextUtil.getUid();
        Long roomId = roomService.addGroup(uid, request);
        return R.success(IdRespVO.builder().id(roomId).build());
    }

	@PostMapping("updateRoomInfo")
	@Operation(summary = "修改群信息")
	public R<Boolean> updateRoomInfo(@Valid @RequestBody RoomInfoReq request) {
		Long uid = ContextUtil.getUid();
		return R.success(roomService.updateRoomInfo(uid, request));
	}

	@PostMapping("updateMyRoomInfo")
	@Operation(summary = "修改'我的'群信息")
	public R<Boolean> updateMyRoomInfo(@Valid @RequestBody RoomMyInfoReq request) {
		Long uid = ContextUtil.getUid();
		return R.success(roomService.updateMyRoomInfo(uid, request));
	}

    @PostMapping("/group/member")
    @Operation(summary ="邀请好友进群")
    public R<Void> addMember(@Valid @RequestBody MemberAddReq request) {
        Long uid = ContextUtil.getUid();
        roomService.addMember(uid, request);
        return R.success();
    }

    @PutMapping("/group/admin")
    @Operation(summary ="添加管理员")
    public R<Boolean> addAdmin(@Valid @RequestBody AdminSetReq request) {
        Long uid = ContextUtil.getUid();
		roomService.addAdmin(uid, request);
        return R.success();
    }

    @DeleteMapping("/group/admin")
    @Operation(summary ="撤销管理员")
    public R<Boolean> revokeAdmin(@Valid @RequestBody AdminSetReq request) {
        Long uid = ContextUtil.getUid();
		roomService.revokeAdmin(uid, request);
        return R.success();
    }

	@Operation(summary = "公告列表")
	@GetMapping("/announcement/list")
	public R<IPage<Announcements>> announcementList(@RequestParam("roomId") Long roomId, @RequestParam("current") Long current,@RequestParam("size") Long size){
		IPage<Announcements> page = new Page<>(current,size);
		return R.success(roomService.announcementList(roomId, page));
	}

	@Operation(summary = "查看公告")
	@GetMapping("/announcement")
	public R announcement(ReadAnnouncementsParam param){
		return R.success(roomService.getAnnouncement(ContextUtil.getUid(), param));
	}

	@Operation(summary = "发布公告")
	@PostMapping("announcement/push")
	public R pushAnnouncement(@Valid @RequestBody AnnouncementsParam param){
		return R.success(roomService.pushAnnouncement(ContextUtil.getUid(), param));
	}

	@Operation(summary = "编辑公告/顶置公告")
	@PostMapping("announcement/edit")
	public R announcementEdit(@Valid @RequestBody AnnouncementsParam param){
		return R.returnResult("编辑", roomService.announcementEdit(ContextUtil.getUid(), param));
	}

	@Operation(summary = "删除公告")
	@PostMapping("announcement/delete")
	public R announcementDelete(@RequestParam("id") Long id){
		return R.success(roomService.announcementDelete(ContextUtil.getUid(), id));
	}

	@Operation(summary = "已读公告")
	@PostMapping("announcement/read")
	public R readAnnouncement(@RequestBody ReadAnnouncementsParam param){
		return R.success(roomService.readAnnouncement(ContextUtil.getUid(), param));
	}

	@Operation(summary = "合并消息")
	@PostMapping("mergeMessage")
	public R<Void> mergeMessage(@Validated @RequestBody MergeMessageReq req){
		roomService.mergeMessage(ContextUtil.getUid(), req);
		return R.success();
	}

	@PostMapping("/group/member/nickname")
	@Operation(summary ="修改群成员昵称（后台专用）")
	public R<Void> updateMemberNickname(@Valid @RequestBody UpdateMemberNicknameReq request) {
		roomService.updateMemberNickname(request);
		return R.success();
	}

	@PostMapping("/group/disband")
	@Operation(summary ="解散群聊（后台专用）")
	public R<Void> disbandGroup(@Valid @RequestBody DisbandGroupReq request) {
		roomService.disbandGroup(request);
		return R.success();
	}

	@Operation(summary = "所有群聊列表（后台管理专用）")
	@GetMapping("/group/all")
	public R<List<MemberResp>> getAllGroupList(){
		return R.success(roomService.getAllGroupList());
	}

	@Operation(summary = "分页查询所有群聊列表（管理员专用，支持按群昵称和群成员昵称搜索）")
	@GetMapping("/group/page")
	public R<PageBaseResp<MemberResp>> getGroupPage(@Valid GroupPageReq req){
		return R.success(roomService.getGroupPage(req));
	}

	@GetMapping("/group/member/page")
	@Operation(summary ="群成员分页列表（简化版）")
	public R<PageBaseResp<GroupMemberSimpleResp>> getGroupMemberPage(@Valid GroupMemberPageReq request) {
		return R.success(roomService.getGroupMemberPage(request));
	}

}
