package com.luohuo.flex.im.core.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.luohuo.flex.im.core.admin.service.AdminStatsService;
import com.luohuo.flex.im.core.chat.dao.RoomGroupDao;
import com.luohuo.flex.im.core.user.dao.BlackDao;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.domain.entity.Black;
import com.luohuo.flex.im.domain.entity.RoomGroup;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.domain.vo.resp.admin.AdminStatsResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 后台管理统计服务实现
 * @author 乾乾
 */
@Service
@Slf4j
public class AdminStatsServiceImpl implements AdminStatsService {

	@Resource
	private UserDao userDao;

	@Resource
	private RoomGroupDao roomGroupDao;

	@Resource
	private BlackDao blackDao;

	@Override
	public AdminStatsResp getHomeStats() {
		// 1. 统计今日活跃用户数
		LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
		LocalDateTime todayEnd = LocalDateTime.now().with(LocalTime.MAX);

		LambdaQueryWrapper<User> activeUserWrapper = new LambdaQueryWrapper<>();
		activeUserWrapper.between(User::getLastOptTime, todayStart, todayEnd);
		Integer todayActiveUser = Math.toIntExact(userDao.count(activeUserWrapper));

		// 2. 统计群聊总数
		LambdaQueryWrapper<RoomGroup> groupWrapper = new LambdaQueryWrapper<>();
		groupWrapper.eq(RoomGroup::getIsDel, false);
		Integer totalGroup = Math.toIntExact(roomGroupDao.count(groupWrapper));

		// 3. 统计黑名单数据
		// 3.1 当前黑名单总数（未过期的）
		LambdaQueryWrapper<Black> blackWrapper = new LambdaQueryWrapper<>();
		blackWrapper.ge(Black::getDeadline, LocalDateTime.now());
		Integer blackCount = Math.toIntExact(blackDao.count(blackWrapper));

		// 3.2 今日新增黑名单
		LambdaQueryWrapper<Black> todayBlackWrapper = new LambdaQueryWrapper<>();
		todayBlackWrapper.between(Black::getCreateTime, todayStart, todayEnd);
		Integer todayNewBlack = Math.toIntExact(blackDao.count(todayBlackWrapper));

		// 3.3 本周新增黑名单
		LocalDateTime weekStart = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1).with(LocalTime.MIN);
		LambdaQueryWrapper<Black> weekBlackWrapper = new LambdaQueryWrapper<>();
		weekBlackWrapper.between(Black::getCreateTime, weekStart, todayEnd);
		Integer weekNewBlack = Math.toIntExact(blackDao.count(weekBlackWrapper));

		// 4. 构建黑名单统计
		AdminStatsResp.BlackStats blackStats = AdminStatsResp.BlackStats.builder()
				.todayNew(todayNewBlack)
				.weekNew(weekNewBlack)
				.total(blackCount)
				.build();

		// 5. AI 统计（假数据，需要对接 AI 服务）
		AdminStatsResp.AiStats aiStats = AdminStatsResp.AiStats.builder()
				.todayCalls(0)
				.weekCalls(0)
				.activeModels(0)
				.build();

		// 6. 构建响应
		return AdminStatsResp.builder()
				.todayActiveUser(todayActiveUser)
				.totalGroup(totalGroup)
				.blackCount(blackCount)
				.aiCallToday(0) // TODO: 对接 AI 服务
				.blackStats(blackStats)
				.aiStats(aiStats)
				.build();
	}
}
