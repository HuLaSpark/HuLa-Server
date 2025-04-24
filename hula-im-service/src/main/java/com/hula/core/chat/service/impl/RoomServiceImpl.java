package com.hula.core.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.common.domain.vo.res.GroupListVO;
import com.hula.core.chat.dao.AnnouncementsDao;
import com.hula.core.chat.dao.AnnouncementsReadRecordDao;
import com.hula.core.chat.domain.entity.Announcements;
import com.hula.core.chat.domain.entity.AnnouncementsReadRecord;
import com.hula.core.chat.domain.vo.request.GroupAddReq;
import com.hula.core.chat.domain.vo.response.AnnouncementsResp;
import com.hula.snowflake.uid.UidGenerator;
import com.hula.snowflake.uid.utils.Base62Encoder;
import com.hula.utils.AssertUtil;
import com.hula.core.chat.dao.GroupMemberDao;
import com.hula.core.chat.dao.RoomDao;
import com.hula.core.chat.dao.RoomFriendDao;
import com.hula.core.chat.dao.RoomGroupDao;
import com.hula.core.chat.domain.entity.GroupMember;
import com.hula.core.chat.domain.entity.Room;
import com.hula.core.chat.domain.entity.RoomFriend;
import com.hula.core.chat.domain.entity.RoomGroup;
import com.hula.core.chat.domain.enums.GroupRoleEnum;
import com.hula.core.chat.domain.enums.RoomTypeEnum;
import com.hula.core.chat.service.RoomService;
import com.hula.core.chat.service.adapter.ChatAdapter;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.service.cache.UserInfoCache;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class RoomServiceImpl implements RoomService {

    private RoomFriendDao roomFriendDao;
    private RoomDao roomDao;
	private UidGenerator uidGenerator;
    private GroupMemberDao groupMemberDao;
	private AnnouncementsDao announcementsDao;
	private AnnouncementsReadRecordDao announcementsReadRecordDao;
    private UserInfoCache userInfoCache;
    private RoomGroupDao roomGroupDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RoomFriend createFriendRoom(List<Long> uidList) {
        AssertUtil.isNotEmpty(uidList, "房间创建失败，好友数量不对");
        AssertUtil.equal(uidList.size(), 2, "房间创建失败，好友数量不对");
        String key = ChatAdapter.generateRoomKey(uidList);

        RoomFriend roomFriend = roomFriendDao.getByKey(key);
        if (Objects.nonNull(roomFriend)) {
            //如果存在房间就恢复，适用于恢复好友场景
            restoreRoomIfNeed(roomFriend);
        } else {
            //新建房间
            Room room = createRoom(RoomTypeEnum.FRIEND);
            roomFriend = createFriendRoom(room.getId(), uidList);
        }
        return roomFriend;
    }

    @Override
    public RoomFriend getFriendRoom(Long uid1, Long uid2) {
        String key = ChatAdapter.generateRoomKey(Arrays.asList(uid1, uid2));
        return roomFriendDao.getByKey(key);
    }

    @Override
    public void disableFriendRoom(List<Long> uidList) {
        AssertUtil.isNotEmpty(uidList, "房间创建失败，好友数量不对");
        AssertUtil.equal(uidList.size(), 2, "房间创建失败，好友数量不对");
        String key = ChatAdapter.generateRoomKey(uidList);
        roomFriendDao.disableRoom(key);
    }

    @Override
	@Transactional(rollbackFor = Exception.class)
    public RoomGroup createGroupRoom(Long uid, GroupAddReq groupAddReq) {
        List<GroupMember> selfGroup = groupMemberDao.getSelfGroup(uid);
        AssertUtil.isTrue(selfGroup.size() < 60, "每个人只能创建五个群");
        User user = userInfoCache.get(uid);
        Room room = createRoom(RoomTypeEnum.GROUP);
        // 插入群
        RoomGroup roomGroup = ChatAdapter.buildGroupRoom(user, room.getId(), groupAddReq.getGroupName());
		roomGroup.setAccount(Base62Encoder.createAccount(uidGenerator.getUid()));
        roomGroupDao.save(roomGroup);
        // 插入群主
        GroupMember leader = GroupMember.builder()
                .role(GroupRoleEnum.LEADER.getType())
                .groupId(roomGroup.getId())
                .uid(uid)
                .build();
        groupMemberDao.save(leader);
        return roomGroup;
    }

    @Override
    public void groupList(Long uid, IPage<GroupListVO> page) {
        roomDao.groupList(uid, page);
    }

    @Override
    public void checkUser(Long uid, Long roomId) {
        AssertUtil.isTrue(roomGroupDao.checkUser(uid,roomId), "您已退出群聊");
    }

	@Override
	public Boolean updateRoomInfo(RoomGroup roomGroup) {
		return roomGroupDao.updateById(roomGroup);
	}

	@Override
	public List<Long> getGroupUsers(Long id, Boolean isSpecial) {
		return groupMemberDao.getGroupUsers(id, isSpecial);
	}

	/**
	 * 更新已读状态
	 */
	@Override
	public Boolean readAnnouncement(Long uid, Long announcementId) {
		return announcementsReadRecordDao.update(new UpdateWrapper<AnnouncementsReadRecord>().lambda()
				.eq(AnnouncementsReadRecord::getUid, uid)
				.eq(AnnouncementsReadRecord::getAnnouncementsId, announcementId)
				.set(AnnouncementsReadRecord::getIsCheck, true));
	}

	@Override
	public AnnouncementsResp getAnnouncement(Long id) {
		AnnouncementsResp resp = new AnnouncementsResp();
		BeanUtils.copyProperties(announcementsDao.getById(id), resp);
		User user = userInfoCache.get(resp.getUid());
		resp.setUName(user.getName());
		return resp;
	}

	@Override
	public Long getAnnouncementReadCount(Long announcementId) {
		return announcementsReadRecordDao.count(new QueryWrapper<AnnouncementsReadRecord>().lambda()
				.eq(AnnouncementsReadRecord::getAnnouncementsId, announcementId));
	}

	@Override
	public Boolean announcementDelete(Long id) {
		return announcementsDao.removeById(id);
	}

	@Override
	public Boolean saveAnnouncements(Announcements announcements) {
		if(announcements.getTop()){
			announcementsDao.update(new UpdateWrapper<Announcements>().set("top", false).eq("top", true));
		}
		return announcementsDao.save(announcements);
	}

	@Override
	public Boolean saveBatchAnnouncementsRecord(List<AnnouncementsReadRecord> announcementsReadRecordList) {
		return announcementsReadRecordDao.saveBatch(announcementsReadRecordList);
	}

	@Override
	public void updateState(Boolean u1, Long uid1, Long uid2, Boolean deFriend) {
		UpdateWrapper<RoomFriend> wrapper = new UpdateWrapper<RoomFriend>()
				.and(w -> w.eq("uid1", uid1).eq("uid2", uid2)).or
						(w -> w.eq("uid1", uid2).eq("uid2", uid1));
		if(u1){
			wrapper.set("de_friend1", deFriend);
		} else {
			wrapper.set("de_friend2", deFriend);
		}
		roomFriendDao.update(wrapper);
	}

	private RoomFriend createFriendRoom(Long roomId, List<Long> uidList) {
        RoomFriend insert = ChatAdapter.buildFriendRoom(roomId, uidList);
        roomFriendDao.save(insert);
        return insert;
    }

    private Room createRoom(RoomTypeEnum typeEnum) {
        Room insert = ChatAdapter.buildRoom(typeEnum);
        roomDao.save(insert);
        return insert;
    }

    private void restoreRoomIfNeed(RoomFriend room) {
        if (room.getDeFriend1() || room.getDeFriend2()) {
            roomFriendDao.restoreRoom(room.getId());
        }
    }

	/**
	 * 创建一个群成员
	 * @param groupId 群id
	 * @param uid     加入的人
	 */
	public void createGroupMember(Long groupId, Long uid) {
		GroupMember groupMember = new GroupMember();
		groupMember.setRemark("");
		groupMember.setGroupId(groupId);
		groupMember.setRole(3);
		groupMember.setUid(uid);
		groupMemberDao.save(groupMember);
	}

	@Override
	public Boolean updateAnnouncement(Announcements announcement) {
		if(announcement.getTop()){
			announcementsDao.update(new UpdateWrapper<Announcements>().set("top", false).eq("top", true));
		}
		return announcementsDao.updateById(announcement);
	}

	@Override
	public IPage<Announcements> announcementList(Long roomId, IPage<Announcements> page) {
		return announcementsDao.getBaseMapper().selectPage(page, new QueryWrapper<Announcements>().eq("room_id", roomId).orderByDesc("publish_time"));
	}
}
