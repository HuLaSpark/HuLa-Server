package com.luohuo.flex.im.core.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.luohuo.flex.im.core.admin.service.AdminStatsService;
import com.luohuo.flex.im.core.chat.dao.RoomGroupDao;
import com.luohuo.flex.im.core.user.dao.BlackDao;
import com.luohuo.flex.im.core.user.dao.UserDao;
import com.luohuo.flex.im.core.user.service.cache.UserCache;
import com.luohuo.flex.im.domain.entity.Black;
import com.luohuo.flex.im.domain.entity.RoomGroup;
import com.luohuo.flex.im.domain.entity.User;
import com.luohuo.flex.im.domain.vo.resp.admin.ActiveUserResp;
import com.luohuo.flex.im.domain.vo.resp.admin.AdminStatsResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

import com.luohuo.flex.im.api.BaseLoginLogApi;
import com.luohuo.flex.im.domain.vo.resp.admin.LoginRankResp;

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
	@Autowired
	private UserCache userCache;
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

        // 6. 最近一个月登录≥3次的用户总数
        LocalDateTime monthStart = LocalDateTime.now().minusDays(30).with(LocalTime.MIN);
        Long month3plus = baseLoginLogApi.countUsersWithMinLogins(monthStart, todayEnd, 3).getData();

        // 7. 构建响应
        return AdminStatsResp.builder()
                .todayActiveUser(todayActiveUser)
                .totalGroup(totalGroup)
                .blackCount(blackCount)
                .aiCallToday(0) // TODO: 对接 AI 服务
                .blackStats(blackStats)
                .aiStats(aiStats)
                .monthlyLogin3PlusUserCount(month3plus != null ? month3plus.intValue() : 0)
                .build();
    }

    @Resource
    private BaseLoginLogApi baseLoginLogApi;

    @Override
    public List<LoginRankResp> getLoginRank(LocalDateTime start, LocalDateTime end, Integer limit) {
        List<BaseLoginLogApi.LoginRankDTO> list = baseLoginLogApi.getLoginRank(start, end, limit).getData();
        Map<Long, User> userMap = list.stream()
                .map(dto -> dto.uid)
                .distinct()
                .map(userDao::getById)
                .filter(u -> u != null)
                .collect(Collectors.toMap(User::getId, u -> u));
        return list.stream().map(dto -> {
            User u = userMap.get(dto.uid);
            return LoginRankResp.builder()
                    .userId(dto.uid)
                    .username(u != null ? u.getAccount() : null)
                    .nickName(u != null ? u.getName() : null)
                    .total(dto.total)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<ActiveUserResp> getActiveUsers(LocalDateTime start, LocalDateTime end, Integer limit) {
        LocalDateTime s = start != null ? start : LocalDateTime.now().minusDays(30).with(LocalTime.MIN);
        LocalDateTime e = end != null ? end : LocalDateTime.now().with(LocalTime.MAX);
        int lim = limit != null ? limit : 200;
        List<BaseLoginLogApi.LoginRankDTO> ranks = baseLoginLogApi.getLoginRank(s, e, lim).getData();
		List<User> list = userCache.getBatch(ranks.stream().map(item -> item.uid).collect(Collectors.toList())).values().stream().toList();

        Map<Long, Long> countMap = ranks == null ? Collections.emptyMap() : ranks.stream().collect(Collectors.toMap(r -> r.uid, r -> r.total, (a, b) -> a));
        List<ActiveUserResp> res = list.stream().map(u -> {
            String ip = null;
            String location = null;
            String isp = null;
            var info = u.getIpInfo();
            if (info != null) {
                ip = info.getUpdateIp();
                var detail = info.getUpdateIpDetail();
                if (detail != null) {
                    if (ip == null) ip = detail.getIp();
                    String country = detail.getCountry();
                    String region = detail.getRegion();
                    String city = detail.getCity();
                    isp = detail.getIsp();
                    StringBuilder sb = new StringBuilder();
                    if (country != null && !country.isEmpty()) sb.append(country);
                    if (region != null && !region.isEmpty()) sb.append(region);
                    if (city != null && !city.isEmpty()) sb.append(city);
                    location = sb.length() > 0 ? sb.toString() : null;
                }
            }
            ActiveUserResp resp = new ActiveUserResp();
            resp.setUsername(u.getAccount());
            resp.setNickName(u.getName());
            resp.setAvatar(u.getAvatar());
            resp.setLastOptTime(u.getLastOptTime());
            resp.setIp(ip);
            resp.setLocation(location);
            resp.setIsp(isp);
            resp.setLoginTimes(countMap.getOrDefault(u.getId(), 0L).intValue());
            return resp;
        }).collect(Collectors.toList());
        res.sort(Comparator.comparing(ActiveUserResp::getLoginTimes, Comparator.nullsFirst(Integer::compareTo)).reversed());
        return res;
    }
}
