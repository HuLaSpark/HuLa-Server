package com.luohuo.flex.areatest;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.im.core.chat.dao.MessageDao;
import com.luohuo.flex.im.core.chat.mapper.Contact222Mapper;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.domain.entity.Message;
import com.luohuo.flex.im.domain.entity.Message2;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.domain.entity.msg.MessageExtra;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Slf4j
public class CopyUserTest {

    @Resource
	DefUserService defUserService;
    @Resource
	UserDao userDao;
	@Resource
	Contact222Mapper contact222Mapper;

	@Test
	public void test2() {
		ContextUtil.setTenantId(1L);
		QueryWrapper<Message2> queryWrapper = new QueryWrapper<Message2>().ge("type", 12);
		List<Message2> messages = contact222Mapper.selectList(queryWrapper);
		for (Message2 message : messages) {
			JSONObject extra = message.getExtra();

			if (extra.containsKey("audioCallMsgDTO")) {
				JSONObject audioCallMsg = extra.getJSONObject("audioCallMsgDTO");

				if(audioCallMsg != null){
					// 删除creator字段
					audioCallMsg.remove("creator");
					// 将修改后的对象放回extra中
					extra.put("audioCallMsgDTO", audioCallMsg);
				}
			}

			if (extra.containsKey("videoCallMsgDTO")) {
				JSONObject audioCallMsg = extra.getJSONObject("videoCallMsgDTO");
				if(audioCallMsg != null){
					// 删除creator字段
					audioCallMsg.remove("creator");
					// 将修改后的对象放回extra中
					extra.put("audioCallMsgDTO", audioCallMsg);
				}
			}

			Message2 message2 = new Message2();
			message2.setId(message.getId());
			message2.setExtra(extra);
			contact222Mapper.updateById(message2);
		}
	}

    /**
	 * 同步用户数据
	 * 需要吧def_user 复制一份到luohuo_im_01 库里面
     */
    @Test
    public void test() {
		ContextUtil.setTenantId(1L);
		List<User> list = userDao.list();

		for (User user : list) {
			DefUser defUser = new DefUser();
			defUser.setUsername(user.getAccount());
			defUser.setNickName(user.getName());
			defUser.setWxOpenId(user.getOpenId());
			defUser.setTenantId(1L);
			defUser.setLastLoginTime(user.getLastOptTime());
			defUser.setEmail(user.getEmail());
			defUser.setAvatar(user.getAvatar());
			defUser.setPasswordErrorNum(0);
			if(user.getIpInfo() != null){
				com.luohuo.flex.model.entity.base.IpInfo ipInfo1 = new com.luohuo.flex.model.entity.base.IpInfo();
				BeanUtils.copyProperties(user.getIpInfo(), ipInfo1);
				defUser.setIpInfo(ipInfo1);
			}
			defUser.setReadonly(false);
			defUser.setSalt(user.getName());
			defUser.setState(true);
			defUser.setSex(user.getSex());
			defUser.setSystemType(2);
			defUser.setCreateTime(user.getCreateTime());
			defUser.setUpdateTime(user.getUpdateTime());
			defUser.setCreateBy(1L);
			defUser.setPassword(SecureUtil.sha256("123456" + defUser.getSalt()));
			defUserService.getSuperManager().save(defUser);

			User user1 = new User();
			user1.setId(user.getId());
			user1.setUserId(defUser.getId());
			userDao.updateById(user1);
		}
	}

}
