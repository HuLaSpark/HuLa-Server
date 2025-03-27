package com.hula.common;

import com.hula.HuLaImServiceApplication;
import com.hula.core.chat.dao.RoomGroupDao;
import com.hula.core.chat.domain.entity.RoomGroup;
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
