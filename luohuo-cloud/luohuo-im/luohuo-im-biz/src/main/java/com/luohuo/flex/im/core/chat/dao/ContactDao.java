package com.luohuo.flex.im.core.chat.dao;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.basic.tenant.core.aop.TenantIgnore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.common.utils.CursorUtils;
import com.luohuo.flex.im.domain.entity.Contact;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.core.chat.mapper.ContactMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

	public List<Contact> get(Long uid, List<Long> roomIdList) {
		return lambdaQuery()
				.eq(Contact::getUid, uid)
				.in(Contact::getRoomId, roomIdList)
				.list();
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
     * 更新所有人的会话时间，没有就直接插入
     */
	@Async
	@TenantIgnore
    public void refreshOrCreateActiveTime(Long roomId, List<Long> memberUidList, Long msgId, LocalDateTime activeTime) {
        baseMapper.refreshOrCreateActiveTime(roomId, memberUidList, msgId, activeTime);
    }

	@TenantIgnore
	public void refreshOrCreateActive(Object roomId, List<Long> memberUidList, Object msgId, Object activeTime) {
		baseMapper.refreshOrCreateActive(roomId, memberUidList, msgId, activeTime);
	}

	public void refreshOrCreate(Long roomId, Long uid) {
		baseMapper.refreshOrCreate(roomId, uid);
	}

	public HashMap<String, Contact> getContactMapByUid(Long uid) {
		// 1. 查出用户要展示的会话列表
		List<Contact> contacts = getAllContactsByUid(uid);

		return contacts.stream()
				.distinct()
				.collect(Collectors.toMap(
						contact -> StrUtil.format("{}_{}", contact.getUid(), contact.getRoomId()),
						contact -> contact,
						(existing, replacement) -> existing,
						HashMap::new
				));
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
		insert.setReadTime(LocalDateTime.now());
		return save(insert);
	}

	public Boolean setHide(Long uid, Long roomId, Boolean hide) {
		return update(new UpdateWrapper<Contact>().lambda()
				.eq(Contact::getRoomId, roomId).eq(Contact::getUid, uid).set(Contact::getHide, hide));
	}

	/**
	 * 查询用户的所有会话
	 * @param uid
	 * @return
	 */
	public List<Contact> getAllContactsByUid(Long uid) {
//		return cachePlusOps.hGet(UserContactCacheKeyBuilder.build(uid), x -> lambdaQuery().eq(Contact::getUid, uid).list(), true).getValue();
		return lambdaQuery().eq(Contact::getUid, uid).list();
	}

}
