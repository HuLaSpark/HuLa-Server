package com.luohuo.flex.areatest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.StaticLog;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.core.chat.dao.ContactDao;
import com.luohuo.flex.im.core.chat.dao.GroupMemberDao;
import com.luohuo.flex.im.core.chat.dao.RoomGroupDao;
import com.luohuo.flex.im.core.chat.service.RoomService;
import com.luohuo.flex.im.core.chat.service.impl.ChatServiceImpl;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.core.user.dao.UserFriendDao;
import com.luohuo.flex.im.core.user.service.impl.FriendServiceImpl;
import com.luohuo.flex.im.domain.entity.Contact;
import com.luohuo.flex.im.domain.entity.GroupMember;
import com.luohuo.flex.im.domain.entity.RoomGroup;
import com.luohuo.flex.im.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.luohuo.flex.base.entity.system.DefArea;

import jakarta.annotation.Resource;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
public class CityParserTest {

    @Resource
    CityParser cityParser;
    @Resource
    SqlCityParserDecorator sqlCityParserDecorator;
	@Resource
	UserDao userDao;
	@Resource
	UserFriendDao userFriendDao;
	@Resource
	ChatServiceImpl chatService;
	@Resource
	private RoomService roomService;
	@Resource
	private GroupMemberDao groupMemberDao;
	@Resource
	private RoomGroupDao roomGroupDao;
	@Resource
	private ContactDao contactDao;
	@Resource
	private FriendServiceImpl friendService;
	@Test
	public void bot() {
		ContextUtil.setUid(1L);
		ContextUtil.setTenantId(1L);
		// 2. 计算会话的最后一条消息id
		List<User> list = userDao.list();
		list.forEach(user -> {
			List<GroupMember> groupList = groupMemberDao.getSelfGroup(user.getId());
			groupList.forEach(groupMember -> {
				RoomGroup roomGroup = roomGroupDao.getById(groupMember.getGroupId());

				if(roomGroup!=null){
					Contact contact = contactDao.get(user.getId(), roomGroup.getRoomId());
					if(contact == null){
						// 创建会话
						chatService.createContact(user.getId(), roomGroup.getRoomId());
					}
				}
			});
		});
	}

		/**
		 * 实时爬取最新的地区数据，请执行该方法
		 */
    @Test
    public void pullArea() {

        TimeInterval timer = DateUtil.timer();
        List<DefArea> list = cityParser.parseProvinces(2);
        long interval = timer.interval();// 花费毫秒数
        long intervalMinute = timer.intervalMinute();// 花费分钟数
        StaticLog.error("爬取数据 花费毫秒数: {} ,   花费分钟数:{} . ", interval, intervalMinute);

//        System.out.println(JSONObject.toJSONString(list, true));

        TimeInterval timer2 = DateUtil.timer();
        // 持久化
        sqlCityParserDecorator.batchSave(list);

        // ---------------------------------
        long interval2 = timer2.interval();// 花费毫秒数
        long intervalMinute2 = timer2.intervalMinute();// 花费分钟数
        StaticLog.error("保存数据 花费毫秒数: {} ,   花费分钟数:{} . ", interval2, intervalMinute2);
    }

}
