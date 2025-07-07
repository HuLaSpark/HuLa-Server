package com.hula.core.chat.service.impl;

import cn.hutool.core.util.StrUtil;
import com.hula.core.chat.service.adapter.RoomAdapter;
import com.hula.core.chat.service.cache.RoomGroupCache;
import com.hula.enums.HttpErrorEnum;
import com.hula.enums.GroupErrorEnum;
import com.hula.utils.AssertUtil;
import com.hula.core.chat.dao.*;
import com.hula.core.chat.domain.entity.Room;
import com.hula.core.chat.domain.entity.RoomGroup;
import com.hula.core.chat.domain.vo.request.admin.AdminAddReq;
import com.hula.core.chat.domain.vo.request.admin.AdminRevokeReq;
import com.hula.core.chat.domain.vo.request.member.MemberExitReq;
import com.hula.core.chat.service.IGroupMemberService;
import com.hula.core.chat.service.adapter.MemberAdapter;
import com.hula.core.chat.service.cache.GroupMemberCache;
import com.hula.core.user.domain.enums.WsBaseResp;
import com.hula.core.user.domain.vo.resp.ws.WSMemberChange;
import com.hula.core.user.service.impl.PushService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.hula.core.chat.constant.GroupConst.MAX_MANAGE_COUNT;

/**
 * 群成员服务类
 * @author nyh
 */
@Service
@AllArgsConstructor
public class GroupMemberServiceImpl implements IGroupMemberService {

    private GroupMemberDao groupMemberDao;
    private RoomDao roomDao;
    private ContactDao contactDao;
    private MessageDao messageDao;
	private RoomGroupCache roomGroupCache;
    private GroupMemberCache groupMemberCache;
    private PushService pushService;

    /**
     * 增加管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    @Override
    public void addAdmin(Long uid, AdminAddReq request) {
        // 1. 判断群聊是否存在
        RoomGroup roomGroup = roomGroupCache.getByRoomId(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

        // 2. 判断该用户是否是群主
        Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
        AssertUtil.isTrue(isLord, GroupErrorEnum.NOT_ALLOWED_OPERATION);

        // 3. 判断群成员是否在群中
        Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId(), request.getUidList());
        AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

        // 4. 判断管理员数量是否达到上限
        // 4.1 查询现有管理员数量
        List<Long> manageUidList = groupMemberDao.getManageUidList(roomGroup.getId());
        // 4.2 去重
        HashSet<Long> manageUidSet = new HashSet<>(manageUidList);
        manageUidSet.addAll(request.getUidList());
        AssertUtil.isFalse(manageUidSet.size() > MAX_MANAGE_COUNT, GroupErrorEnum.MANAGE_COUNT_EXCEED);

        // 5. 增加管理员
        groupMemberDao.addAdmin(roomGroup.getId(), request.getUidList());
    }

    /**
     * 撤销管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    @Override
    public void revokeAdmin(Long uid, AdminRevokeReq request) {
        // 1. 判断群聊是否存在
        RoomGroup roomGroup = roomGroupCache.getByRoomId(request.getRoomId());
        AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

        // 2. 判断该用户是否是群主
        Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
        AssertUtil.isTrue(isLord, GroupErrorEnum.NOT_ALLOWED_OPERATION);

        // 3. 判断群成员是否在群中
        Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId(), request.getUidList());
        AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

        // 4. 撤销管理员
        groupMemberDao.revokeAdmin(roomGroup.getId(), request.getUidList());
    }

    /**
     * 退出群聊 | 解散群聊
     *
     * @param uid     需要退出的用户ID
     * @param request 请求信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exitGroup(Long uid, MemberExitReq request) {
        Long roomId = request.getRoomId();
        // 1. 判断群聊是否存在
        RoomGroup roomGroup = roomGroupCache.getByRoomId(roomId);
        AssertUtil.isNotEmpty(roomGroup, GroupErrorEnum.GROUP_NOT_EXIST);

        // 2. 判断房间是否是大群聊 （大群聊禁止退出）
        Room room = roomDao.getById(roomId);
        AssertUtil.isFalse(room.isHotRoom(), GroupErrorEnum.NOT_ALLOWED_FOR_EXIT_GROUP);

        // 3. 判断群成员是否在群中
        Boolean isGroupShip = groupMemberDao.isGroupShip(roomGroup.getRoomId(), Collections.singletonList(uid));
        AssertUtil.isTrue(isGroupShip, GroupErrorEnum.USER_NOT_IN_GROUP);

        // 4. 判断该用户是否是群主
        Boolean isLord = groupMemberDao.isLord(roomGroup.getId(), uid);
        if (isLord) {
            // 4.1 删除房间和群并清除缓存
            boolean isDelRoom = roomDao.removeById(roomId);
			roomGroupCache.removeById(roomGroup.getId());
			roomGroupCache.evictGroup(roomGroup.getAccount());
			if(StrUtil.isNotEmpty(request.getAccount())){
				roomGroupCache.evictGroup(request.getAccount());
			}
            AssertUtil.isTrue(isDelRoom, HttpErrorEnum.SYSTEM_ERROR);
            // 4.2 删除会话
            Boolean isDelContact = contactDao.removeByRoomId(roomId, Collections.EMPTY_LIST);
            AssertUtil.isTrue(isDelContact, HttpErrorEnum.SYSTEM_ERROR);
            // 4.3 获取并删除群成员
			List<Long> memberUidList = groupMemberDao.getMemberUidList(roomGroup.getId(), null);
            Boolean isDelGroupMember = groupMemberDao.removeByGroupId(roomGroup.getId(), Collections.EMPTY_LIST);
            AssertUtil.isTrue(isDelGroupMember, HttpErrorEnum.SYSTEM_ERROR);
            // 4.4 删除消息记录 (逻辑删除)
            Boolean isDelMessage = messageDao.removeByRoomId(roomId, Collections.EMPTY_LIST);
            AssertUtil.isTrue(isDelMessage, HttpErrorEnum.SYSTEM_ERROR);
			// 4.5 告知所有人群已经被解散, 这里要走groupMemberDao查询，缓存中可能没有屏蔽群的用户
			groupMemberCache.evictMemberUidList(room.getId());
			groupMemberCache.evictAllMemberDetails();
			pushService.sendPushMsg(RoomAdapter.buildGroupDissolution(roomGroup.getName()), memberUidList, uid);
        } else {
            // 4.6 删除会话
            Boolean isDelContact = contactDao.removeByRoomId(roomId, Collections.singletonList(uid));
            AssertUtil.isTrue(isDelContact, HttpErrorEnum.SYSTEM_ERROR);
            // 4.7 删除群成员
            Boolean isDelGroupMember = groupMemberDao.removeByGroupId(roomGroup.getId(), Collections.singletonList(uid));
            AssertUtil.isTrue(isDelGroupMember, HttpErrorEnum.SYSTEM_ERROR);
            // 4.8 发送移除事件告知群成员
            List<Long> memberUidList = groupMemberCache.getMemberExceptUidList(roomGroup.getRoomId());
            WsBaseResp<WSMemberChange> ws = MemberAdapter.buildMemberRemoveWS(roomGroup.getRoomId(), uid);
            pushService.sendPushMsg(ws, memberUidList, uid);
            groupMemberCache.evictMemberUidList(room.getId());
			groupMemberCache.evictMemberDetail(room.getId(), uid);
        }
    }
}
