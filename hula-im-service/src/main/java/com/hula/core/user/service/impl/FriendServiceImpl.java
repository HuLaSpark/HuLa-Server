package com.hula.core.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Lists;
import com.hula.common.annotation.RedissonLock;
import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.req.PageBaseReq;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.common.domain.vo.res.PageBaseResp;
import com.hula.common.event.UserApplyEvent;
import com.hula.common.event.UserApprovalEvent;
import com.hula.core.chat.dao.RoomFriendDao;
import com.hula.core.chat.domain.entity.RoomFriend;
import com.hula.core.chat.service.ChatService;
import com.hula.core.chat.service.ContactService;
import com.hula.core.chat.service.RoomService;
import com.hula.core.chat.service.adapter.MessageAdapter;
import com.hula.core.user.dao.UserApplyDao;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.dao.UserFriendDao;
import com.hula.core.user.domain.dto.RequestApprovalDto;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.entity.UserApply;
import com.hula.core.user.domain.entity.UserFriend;
import com.hula.core.user.domain.enums.ApplyDeletedEnum;
import com.hula.core.user.domain.enums.ApplyStatusEnum;
import com.hula.core.user.domain.vo.req.friend.FriendApplyReq;
import com.hula.core.user.domain.vo.req.friend.FriendApproveReq;
import com.hula.core.user.domain.vo.req.friend.FriendCheckReq;
import com.hula.core.user.domain.vo.resp.friend.FriendApplyResp;
import com.hula.core.user.domain.vo.resp.friend.FriendCheckResp;
import com.hula.core.user.domain.vo.resp.friend.FriendResp;
import com.hula.core.user.domain.vo.resp.friend.FriendUnreadResp;
import com.hula.core.user.service.FriendService;
import com.hula.core.user.service.adapter.FriendAdapter;
import com.hula.utils.AssertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hula.core.user.domain.enums.ApplyStatusEnum.WAIT_APPROVAL;

/**
 * @author nyh
 */
@Slf4j
@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService {

    private UserFriendDao userFriendDao;
    private UserApplyDao userApplyDao;
    private ApplicationEventPublisher applicationEventPublisher;
    private RoomService roomService;
    private ContactService contactService;
    private ChatService chatService;
    private UserDao userDao;
    private RoomFriendDao roomFriendDao;

    /**
     * 检查
     * 检查是否是自己好友
     *
     * @param uid     uid
     * @param request 请求
     * @return {@link FriendCheckResp}
     */
    @Override
    public FriendCheckResp check(Long uid, FriendCheckReq request) {
        List<UserFriend> friendList = userFriendDao.getByFriends(uid, request.getUidList());

        Set<Long> friendUidSet = friendList.stream().map(UserFriend::getFriendUid).collect(Collectors.toSet());
        List<FriendCheckResp.FriendCheck> friendCheckList = request.getUidList().stream().map(friendUid -> {
            FriendCheckResp.FriendCheck friendCheck = new FriendCheckResp.FriendCheck();
            friendCheck.setUid(friendUid);
            friendCheck.setIsFriend(friendUidSet.contains(friendUid));
            return friendCheck;
        }).collect(Collectors.toList());
        return new FriendCheckResp(friendCheckList);
    }

    /**
     * 申请好友
     *
     * @param request 请求
     */
    @Override
    @RedissonLock(key = "#uid")
    public void apply(Long uid, FriendApplyReq request) {
        //是否有好友关系
        UserFriend friend = userFriendDao.getByFriend(uid, request.getTargetUid());
        AssertUtil.isEmpty(friend, "你们已经是好友了");
        // 是否有待审批的申请记录(自己的)
        UserApply selfApproving = userApplyDao.getFriendApproving(uid, request.getTargetUid(), true);
        if (Objects.nonNull(selfApproving)) {
            log.info("已有好友申请记录,uid:{}, targetId:{}", uid, request.getTargetUid());
            return;
        }
        // 是否有待审批的申请记录(别人请求自己的)
        UserApply friendApproving = userApplyDao.getFriendApproving(request.getTargetUid(), uid, false);
        if (Objects.nonNull(friendApproving)) {
            SpringUtil.getBean(this.getClass()).applyApprove(uid, new FriendApproveReq(friendApproving.getId()));
            return;
        }
        // 申请入库
        UserApply newApply = FriendAdapter.buildFriendApply(uid, request);
        userApplyDao.save(newApply);
        // 申请事件
        applicationEventPublisher.publishEvent(new UserApplyEvent(this, newApply));
    }

    /**
     * 分页查询好友申请
     *
     * @param request 请求
     * @return {@link PageBaseResp}<{@link FriendApplyResp}>
     */
    @Override
    public PageBaseResp<FriendApplyResp> pageApplyFriend(Long uid, PageBaseReq request) {
        IPage<UserApply> userApplyIPage = userApplyDao.friendApplyPage(uid, request.plusPage());
        if (CollectionUtil.isEmpty(userApplyIPage.getRecords())) {
            return PageBaseResp.empty();
        }
        // 将这些申请列表设为已读
        readApples(uid, userApplyIPage);
        // 返回消息
        return PageBaseResp.init(userApplyIPage, FriendAdapter.buildFriendApplyList(userApplyIPage.getRecords()));
    }

    private void readApples(Long uid, IPage<UserApply> userApplyIpage) {
        List<Long> applyIds = userApplyIpage.getRecords()
                .stream().map(UserApply::getId)
                .collect(Collectors.toList());
        ;
        userApplyDao.readApples(uid, applyIds);
    }

    /**
     * 申请未读数
     *
     * @return {@link FriendUnreadResp}
     */
    @Override
    public FriendUnreadResp unread(Long uid) {
        Integer unReadCount = userApplyDao.getUnReadCount(uid);
        return new FriendUnreadResp(unReadCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @RedissonLock(key = "#uid")
    public void applyApprove(Long uid, FriendApproveReq request) {
        UserApply userApply = userApplyDao.getById(request.getApplyId());
        AssertUtil.isNotEmpty(userApply, "不存在申请记录");
        AssertUtil.equal(userApply.getTargetId(), uid, "不存在申请记录");
        AssertUtil.equal(userApply.getStatus(), WAIT_APPROVAL.getCode(), "已同意好友申请");
        // 同意申请
        userApplyDao.agree(request.getApplyId());
        //创建双方好友关系
        createFriend(uid, userApply.getUid());
        // 创建一个聊天房间
        RoomFriend roomFriend = roomService.createFriendRoom(Arrays.asList(uid, userApply.getUid()));
        // 通知请求方已处理好友申请
        applicationEventPublisher.publishEvent(new UserApprovalEvent(this, RequestApprovalDto.builder().uid(uid).targetUid(userApply.getUid()).build()));
        // 发送一条同意消息。。我们已经是好友了，开始聊天吧
        chatService.sendMsg(MessageAdapter.buildAgreeMsg(roomFriend.getRoomId()), uid);
    }

    /**
     * 删除好友
     *
     * @param uid       uid
     * @param friendUid 朋友uid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFriend(Long uid, Long friendUid) {
        List<UserFriend> userFriends = userFriendDao.getUserFriend(uid, friendUid);
        if (CollectionUtil.isEmpty(userFriends)) {
            log.info("没有好友关系：{},{}", uid, friendUid);
            return;
        }
        List<Long> friendRecordIds = userFriends.stream().map(UserFriend::getId).collect(Collectors.toList());
        userFriendDao.removeByIds(friendRecordIds);
        // 禁用房间
        roomService.disableFriendRoom(Arrays.asList(uid, friendUid));
    }

    @Override
    public CursorPageBaseResp<FriendResp> friendList(Long uid, CursorPageBaseReq request) {
        CursorPageBaseResp<UserFriend> friendPage = userFriendDao.getFriendPage(uid, request);
        if (CollectionUtils.isEmpty(friendPage.getList())) {
            return CursorPageBaseResp.empty();
        }
        List<Long> friendUids = friendPage.getList()
                .stream().map(UserFriend::getFriendUid)
                .collect(Collectors.toList());
        List<User> userList = userDao.getFriendList(friendUids);
        return CursorPageBaseResp.init(friendPage, FriendAdapter.buildFriend(friendPage.getList(), userList), 0L);
    }

    @Override
    public void reject(Long uid, FriendApproveReq request) {
        UserApply userApply = checkRecord(request);
        userApplyDao.updateStatus(request.getApplyId(), ApplyStatusEnum.REJECT);
    }

    @Override
    public void ignore(Long uid, FriendApproveReq request) {
        checkRecord(request);
        userApplyDao.updateStatus(request.getApplyId(), ApplyStatusEnum.IGNORE);
    }

    @Override
    @RedissonLock(key = "#uid")
    public void deleteApprove(Long uid, FriendApproveReq request) {
        UserApply userApply = checkRecord(request);
        ApplyDeletedEnum deletedEnum;
        //判断谁删了这条记录
        if (Objects.equals(userApply.getUid(), uid)) {
            deletedEnum = ApplyDeletedEnum.APPLY_DELETED;
        } else {
            deletedEnum = ApplyDeletedEnum.TARGET_DELETED;
        }
        //数据库已经当前删除方删了
        if (Objects.equals(userApply.getDeleted(), deletedEnum.getCode())) {
            return;
        }
        //如果之前任意一方删除过 那就是都删了
        if (!Objects.equals(userApply.getDeleted(), ApplyDeletedEnum.NORMAL.getCode())) {
            deletedEnum = ApplyDeletedEnum.ALL_DELETED;
        }
        userApplyDao.deleteApprove(request.getApplyId(), deletedEnum);
    }

    private UserApply checkRecord(FriendApproveReq request) {
        UserApply userApply = userApplyDao.getById(request.getApplyId());
        AssertUtil.isNotEmpty(userApply, "不存在申请记录");
        AssertUtil.equal(userApply.getStatus(), WAIT_APPROVAL.getCode(), "对方已是您的好友");
        return userApply;
    }

    private void createFriend(Long uid, Long targetUid) {
        UserFriend userFriend1 = new UserFriend();
        userFriend1.setUid(uid);
        userFriend1.setFriendUid(targetUid);
        UserFriend userFriend2 = new UserFriend();
        userFriend2.setUid(targetUid);
        userFriend2.setFriendUid(uid);
        userFriendDao.saveBatch(Lists.newArrayList(userFriend1, userFriend2));
    }

}
