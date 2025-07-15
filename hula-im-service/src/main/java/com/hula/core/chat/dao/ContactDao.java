package com.hula.core.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.common.domain.vo.req.CursorPageBaseReq;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.common.utils.CursorUtils;
import com.hula.core.chat.domain.entity.Contact;
import com.hula.core.chat.domain.entity.Message;
import com.hula.core.chat.mapper.ContactMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 会话列表 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class ContactDao extends ServiceImpl<ContactMapper, Contact> {

    public Contact get(Long uid, Long roomId) {
        return lambdaQuery()
                .eq(Contact::getUid, uid)
                .eq(Contact::getRoomId, roomId)
                .one();
    }

    public Integer getReadCount(Message message) {
        return Math.toIntExact(lambdaQuery()
				.eq(Contact::getHide, false)
                .eq(Contact::getRoomId, message.getRoomId())
                .ne(Contact::getUid, message.getFromUid())// 不需要查询出自己
                .ge(Contact::getReadTime, message.getCreateTime())
                .count());
    }

    public Integer getTotalCount(Long roomId) {
        return Math.toIntExact(lambdaQuery()
				.eq(Contact::getHide, false)
                .eq(Contact::getRoomId, roomId)
                .count());
    }

    public Integer getUnReadCount(Message message) {
        return Math.toIntExact(lambdaQuery()
				.eq(Contact::getHide, false)
                .eq(Contact::getRoomId, message.getRoomId())
                .lt(Contact::getReadTime, message.getCreateTime())
                .count());
    }

    public CursorPageBaseResp<Contact> getReadPage(Message message, CursorPageBaseReq cursorPageBaseReq) {
        return CursorUtils.getCursorPageByMysql(this, cursorPageBaseReq, wrapper -> {
            wrapper.eq(Contact::getRoomId, message.getRoomId());
			wrapper.eq(Contact::getHide, false);
            wrapper.ne(Contact::getUid, message.getFromUid());// 不需要查询出自己
            wrapper.ge(Contact::getReadTime, message.getCreateTime());// 已读时间大于等于消息发送时间
        }, Contact::getReadTime);
    }

    public CursorPageBaseResp<Contact> getUnReadPage(Message message, CursorPageBaseReq cursorPageBaseReq) {
        return CursorUtils.getCursorPageByMysql(this, cursorPageBaseReq, wrapper -> {
            wrapper.eq(Contact::getRoomId, message.getRoomId());
			wrapper.eq(Contact::getHide, false);
            wrapper.ne(Contact::getUid, message.getFromUid());// 不需要查询出自己
            wrapper.lt(Contact::getReadTime, message.getCreateTime());// 已读时间小于消息发送时间
        }, Contact::getReadTime);
    }

    /**
     * 获取用户会话列表
     */
    public CursorPageBaseResp<Contact> getContactPage(Long uid, CursorPageBaseReq request) {
        return CursorUtils.getCursorPageByMysql(this, request, wrapper -> wrapper.eq(Contact::getUid, uid).eq(Contact::getHide, false), Contact::getActiveTime);
    }

	/**
	 * 查询用户的所有会话
	 * @param uid
	 * @return
	 */
	public List<Contact> getAllContactsByUid(Long uid) {
		return lambdaQuery().eq(Contact::getUid, uid).list();
	}

    public List<Contact> getAllContactsByUid(List<Long> roomIds, Long uid) {
        return lambdaQuery()
                .in(Contact::getRoomId, roomIds)
				.eq(Contact::getHide, false)
                .eq(Contact::getUid, uid)
                .list();
    }

    /**
     * 更新所有人的会话时间，没有就直接插入
     */
    public void refreshOrCreateActiveTime(Long roomId, List<Long> memberUidList, Long msgId, Date activeTime) {
        baseMapper.refreshOrCreateActiveTime(roomId, memberUidList, msgId, activeTime);
    }

    /**
     * 根据房间ID删除会话
     *
     * @param roomId  房间ID
     * @param uidList 群成员列表
     * @return 是否删除成功
     */
    public Boolean removeByRoomId(Long roomId, List<Long> uidList) {
		LambdaQueryWrapper<Contact> wrapper = new QueryWrapper<Contact>().lambda().eq(Contact::getRoomId, roomId);
        if (CollectionUtil.isNotEmpty(uidList)) {
			wrapper.in(Contact::getUid, uidList);
        }
        return this.remove(wrapper);
    }

	/**
	 * 创建会话
	 */
	public Boolean save(Long uid, Long roomId) {
		Contact insert = new Contact();
		insert.setUid(uid);
		insert.setRoomId(roomId);
		insert.setReadTime(new Date());
		return save(insert);
	}

	public Boolean setHide(Long uid, Long roomId, Boolean hide) {
		return update(new UpdateWrapper<Contact>().lambda()
				.eq(Contact::getRoomId, roomId).eq(Contact::getUid, uid).set(Contact::getHide, hide));
	}

	public Map<Long, Long> getLastMsgIds(Long receiveUid, List<Long> roomIds) {
		return baseMapper.getLastMsgIds(receiveUid, roomIds);
	}
}
