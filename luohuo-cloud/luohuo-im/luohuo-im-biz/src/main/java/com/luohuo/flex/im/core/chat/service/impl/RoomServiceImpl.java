package com.luohuo.flex.im.core.chat.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baidu.fsg.uid.Base62Encoder;
import com.baidu.fsg.uid.UidGenerator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luohuo.basic.validator.utils.AssertUtil;
import com.luohuo.flex.im.core.chat.dao.AnnouncementsDao;
import com.luohuo.flex.im.core.chat.dao.AnnouncementsReadRecordDao;
import com.luohuo.flex.im.core.chat.dao.ContactDao;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.domain.entity.*;
import com.luohuo.flex.im.domain.vo.req.room.GroupPageReq;
import com.luohuo.flex.im.domain.vo.request.GroupAddReq;
import com.luohuo.flex.im.domain.vo.res.PageBaseResp;
import com.luohuo.flex.im.domain.vo.response.AnnouncementsResp;
import com.luohuo.flex.im.core.chat.dao.GroupMemberDao;
import com.luohuo.flex.im.core.chat.dao.RoomDao;
import com.luohuo.flex.im.core.chat.dao.RoomFriendDao;
import com.luohuo.flex.im.core.chat.dao.RoomGroupDao;
import com.luohuo.flex.im.domain.enums.GroupRoleEnum;
import com.luohuo.flex.im.domain.enums.RoomTypeEnum;
import com.luohuo.flex.im.core.chat.service.RoomService;
import com.luohuo.flex.im.core.chat.service.adapter.ChatAdapter;
import com.luohuo.flex.im.core.user.service.cache.UserCache;
import com.luohuo.flex.im.domain.vo.response.MemberResp;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoomServiceImpl implements RoomService {

    private RoomFriendDao roomFriendDao;
	private ContactDao contactDao;
    private RoomDao roomDao;
	private UidGenerator uidGenerator;
    private GroupMemberDao groupMemberDao;
	private AnnouncementsDao announcementsDao;
	private AnnouncementsReadRecordDao announcementsReadRecordDao;
    private UserCache userCache;
    private RoomGroupDao roomGroupDao;
	private UserDao userDao;

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
    public void disableFriendRoom(Long roomId, Long uid, Long friendUid) {
		contactDao.removeByRoomId(roomId, Arrays.asList(uid));
        String key = ChatAdapter.generateRoomKey(Arrays.asList(uid, friendUid));
        roomFriendDao.disableRoom(key);
    }

    @Override
    public RoomGroup createGroupRoom(Long uid, GroupAddReq groupAddReq) {
        List<GroupMember> selfGroup = groupMemberDao.getSelfGroup(uid);
        AssertUtil.isTrue(selfGroup.size() < 20, "每个人只能创建20个群");
        User user = userCache.get(uid);
        Room room = createRoom(RoomTypeEnum.GROUP);
        // 插入群
        RoomGroup roomGroup = ChatAdapter.buildGroupRoom(user, room.getId(), groupAddReq.getGroupName());
		roomGroup.setAccount(Base62Encoder.createGroup(uidGenerator.getUid()));
        roomGroupDao.save(roomGroup);
        // 插入群主
        GroupMember leader = GroupMember.builder()
                .roleId(GroupRoleEnum.LEADER.getType())
                .groupId(roomGroup.getId())
                .uid(uid)
                .build();
        groupMemberDao.save(leader);
        return roomGroup;
    }

    @Override
    public List<MemberResp> groupList(Long uid) {
		return roomDao.groupList(uid);
    }

    @Override
    public List<MemberResp> getAllGroupList() {
        return roomDao.getAllGroupList();
    }

	@Override
	public PageBaseResp<MemberResp> getGroupPage(GroupPageReq req) {
		// 去掉前后空格
		String groupNameKeyword = StrUtil.trim(req.getGroupNameKeyword());
		String memberNameKeyword = StrUtil.trim(req.getMemberNameKeyword());

		// 查询所有群聊
		LambdaQueryWrapper<RoomGroup> wrapper = new LambdaQueryWrapper<>();
		wrapper.orderByDesc(RoomGroup::getCreateTime);
		Page<RoomGroup> page = roomGroupDao.page(req.plusPage(), wrapper);

		// 如果没有搜索关键词，直接返回所有群聊
		if (StrUtil.isBlank(groupNameKeyword) && StrUtil.isBlank(memberNameKeyword)) {
			List<MemberResp> list = page.getRecords().stream()
					.map(roomGroup -> {
						MemberResp resp = new MemberResp();
						resp.setRoomId(roomGroup.getRoomId());
						resp.setGroupId(roomGroup.getId());
						resp.setGroupName(roomGroup.getName());
						resp.setAccount(roomGroup.getAccount());
						resp.setAvatar(roomGroup.getAvatar());
						resp.setAllowScanEnter(roomGroup.getAllowScanEnter());
						return resp;
					})
					.collect(Collectors.toList());

			return PageBaseResp.init((int) page.getCurrent(), (int) page.getSize(), page.getTotal(), list);
		}

		// 有搜索关键词，需要按群昵称和/或群成员昵称过滤
		List<MemberResp> filteredList = page.getRecords().stream()
				.map(roomGroup -> {
					MemberResp resp = new MemberResp();
					resp.setRoomId(roomGroup.getRoomId());
					resp.setGroupId(roomGroup.getId());
					resp.setGroupName(roomGroup.getName());
					resp.setAccount(roomGroup.getAccount());
					resp.setAvatar(roomGroup.getAvatar());
					resp.setAllowScanEnter(roomGroup.getAllowScanEnter());
					return resp;
				})
				.filter(resp -> {
					boolean matchGroupName = true;
					boolean matchMemberName = true;

					// 如果有群昵称关键词，检查群昵称是否匹配
					if (StrUtil.isNotBlank(groupNameKeyword)) {
						matchGroupName = StrUtil.isNotBlank(resp.getGroupName()) &&
								StrUtil.contains(resp.getGroupName(), groupNameKeyword);
					}

					// 如果有群成员昵称关键词，检查群成员昵称是否匹配
					if (StrUtil.isNotBlank(memberNameKeyword)) {
						List<Long> memberUidList = groupMemberDao.getMemberUidList(resp.getGroupId(), null);
						if (memberUidList != null && !memberUidList.isEmpty()) {
							// 查询所有成员的用户信息
							List<User> users = userDao.listByIds(memberUidList);
							// 检查是否有成员昵称包含搜索关键词
							matchMemberName = users.stream()
									.anyMatch(user -> StrUtil.isNotBlank(user.getName()) &&
											StrUtil.contains(user.getName(), memberNameKeyword));
						} else {
							matchMemberName = false;
						}
					}

					// 两个条件都要满足（AND关系）
					return matchGroupName && matchMemberName;
				})
				.collect(Collectors.toList());

		return PageBaseResp.init((int) page.getCurrent(), (int) page.getSize(), (long) filteredList.size(), filteredList);
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
		Announcements announcements = announcementsDao.getById(id);
		resp.setUid(announcements.getUid());
		resp.setRoomId(announcements.getRoomId());
		resp.setTop(announcements.getTop());
		resp.setContent(announcements.getContent());
		resp.setCreateTime(announcements.getCreateTime());
		resp.setId(announcements.getId());
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
			announcementsDao.update(new UpdateWrapper<Announcements>().set("top", false).eq("top", true).eq("room_id", announcements.getRoomId()));
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
		groupMember.setRoleId(GroupRoleEnum.MEMBER.getType());
		groupMember.setUid(uid);
		groupMember.setCreateBy(uid);
		groupMemberDao.save(groupMember);
	}

	@Override
	public Boolean updateAnnouncement(Announcements announcement) {
		if(announcement.getTop()){
			announcementsDao.update(new UpdateWrapper<Announcements>().set("top", false).eq("top", true).eq("room_id", announcement.getRoomId()));
		}
		return announcementsDao.updateById(announcement);
	}

	@Override
	public Room getById(Long roomId) {
		return roomDao.getById(roomId);
	}

	@Override
	public boolean removeById(Long roomId) {
		return roomDao.removeById(roomId);
	}

	@Override
	public IPage<Announcements> announcementList(Long roomId, IPage<Announcements> page) {
		return announcementsDao.getBaseMapper().selectPage(page, new QueryWrapper<Announcements>().eq("room_id", roomId).orderByDesc("top").orderByDesc("create_time"));
	}
}
