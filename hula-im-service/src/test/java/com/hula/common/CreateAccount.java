package com.hula.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hula.HuLaImServiceApplication;
import com.hula.core.chat.dao.ContactDao;
import com.hula.core.chat.dao.RoomGroupDao;
import com.hula.core.chat.domain.entity.Contact;
import com.hula.core.chat.domain.entity.Room;
import com.hula.core.chat.domain.entity.RoomFriend;
import com.hula.core.chat.domain.entity.RoomGroup;
import com.hula.core.chat.domain.enums.RoomTypeEnum;
import com.hula.core.chat.service.cache.GroupMemberCache;
import com.hula.core.chat.service.cache.RoomCache;
import com.hula.core.chat.service.cache.RoomFriendCache;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.snowflake.uid.UidGenerator;
import com.hula.snowflake.uid.utils.Base62Encoder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest(classes = HuLaImServiceApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class CreateAccount {
	@Resource
	private UserDao userDao;
    @Resource
    private RoomGroupDao roomGroupDao;
	@Resource
	private UidGenerator uidGenerator;

	@Resource
	private ContactDao contactDao;

	@Resource
	private RoomCache roomCache;

	@Resource
	private RoomFriendCache roomFriendCache;

	@Resource
	private GroupMemberCache groupMemberCache;

	/**
	 * 移除不存在的会话
	 */
	@Test
	public void removeContacts() {
		List<Contact> contacts = contactDao.getBaseMapper().selectList(new QueryWrapper<>());

		// 需要移除的会话
		List<Long> ids = new ArrayList<>();
		for (Contact contact : contacts) {
			Room room = roomCache.get(contact.getRoomId());
			if(room == null){
				ids.add(contact.getId());
			} else {
				if(room.getType().equals(RoomTypeEnum.FRIEND.getType())){
					RoomFriend roomFriend = roomFriendCache.get(contact.getRoomId());
					if(roomFriend == null || !roomFriend.getUid1().equals(contact.getUid()) && !roomFriend.getUid2().equals(contact.getUid())){
						ids.add(contact.getId());
					}
				} else {
					List<Long> uidList = groupMemberCache.getMemberUidList(contact.getRoomId());
					if(uidList.isEmpty() || !uidList.contains(contact.getUid())){
						ids.add(contact.getId());
					}
				}
			}
		}
		System.out.println("需要移除的会话：" + ids);
	}


	/**
	 * 批量填充群账号
	 */
    @Test
    public void createGroupAccountCode() throws InterruptedException {
		List<RoomGroup> list = roomGroupDao.list();
		for (RoomGroup roomGroup : list) {
			roomGroup.setAccount(Base62Encoder.createAccount(uidGenerator.getUid()));
			Thread.sleep(50);
		}
		roomGroupDao.updateBatchById(list);
	}

	/**
	 * 批量填充用户账号
	 * @throws InterruptedException
	 */
	@Test
	public void createUserAccountCode() throws InterruptedException {
		List<User> list = userDao.list();
		for (User user : list) {
			user.setAccount(Base62Encoder.createAccount(uidGenerator.getUid()));
			Thread.sleep(50);
		}
		userDao.updateBatchById(list);
	}

}
