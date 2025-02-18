package com.hula.core.chat.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.common.domain.po.RoomChatInfoPO;
import com.hula.common.domain.vo.res.GroupListVO;
import com.hula.core.chat.domain.entity.RoomFriend;
import com.hula.core.chat.domain.entity.RoomGroup;
import com.hula.core.chat.domain.vo.request.GroupAddReq;

import java.util.List;

/**
 * 房间底层管理
 * @author nyh
 */
public interface RoomService {

    /**
     * 创建一个单聊房间
     */
    RoomFriend createFriendRoom(List<Long> uidList);

    RoomFriend getFriendRoom(Long uid1, Long uid2);

    /**
     * 禁用一个单聊房间
     */
    void disableFriendRoom(List<Long> uidList);


    /**
     * 创建一个群聊房间
     */
    RoomGroup createGroupRoom(Long uid, GroupAddReq groupAddReq);


    List<RoomChatInfoPO> chatInfo(Long uid, List<Long> roomIds, int type);

    void groupList(Long uid, IPage<GroupListVO> page);

    /**
     * 校验当前用户是否在群里
     * @param uid 当前用户
     * @param roomId 房间id
     */
    void checkUser(Long uid, Long roomId);

}
