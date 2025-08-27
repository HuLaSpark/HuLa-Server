package com.luohuo.flex;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.luohuo.basic.base.R;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.flex.common.cache.PresenceCacheKeyBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * IM在线人员服务 [单个群人员大于10万时需要开启redis分片存储、分片查询]
 *
 * @author 乾乾
 */
@Slf4j
@RestController
@RequestMapping("/online")
@AllArgsConstructor
@Tag(name = "在线服务")
public class OnlineController {

	private CachePlusOps cachePlusOps;

	/**
	 * 查询在线用户ID集合
	 * @param uids 待查询的用户ID列表
	 * @return 在线的用户ID集合
	 */
	@PostMapping("/user/online-users-list")
	public R<Set<Long>> getOnlineUsersList(@RequestBody List<Long> uids) {
		if (CollUtil.isEmpty(uids)) {
			return R.success(Collections.emptySet());
		}

		String onlineKey = PresenceCacheKeyBuilder.globalOnlineUsersKey().getKey();
		Set<Long> onlineUsers = new HashSet<>();

		// 分页批量查询
		Lists.partition(uids, 500).forEach(batch -> {
			// 1. 批量查询分数
			List<Object> scores = cachePlusOps.getZSetScores(onlineKey, batch);

			// 2. 过滤在线用户
			for (int i = 0; i < batch.size(); i++) {
				if (scores.get(i) != null) {
					onlineUsers.add(batch.get(i));
				}
			}
		});

		return R.success(onlineUsers);
	}

	/**
	 * 查询在线的人员
	 * @param uids
	 * @return 返回用户的在线状态
	 */
	@PostMapping("/user/online-status")
	public R<Map<Long, Boolean>> getUsersOnlineStatus(@RequestBody List<Long> uids) {
		String onlineKey = PresenceCacheKeyBuilder.globalOnlineUsersKey().getKey();
		Map<Long, Boolean> statusMap = new HashMap<>();

		// 1. 管道批量查询分数
		List<Object> scores = cachePlusOps.getZSetScores(onlineKey, uids);

		// 2. 判定在线状态：分数非空 = 在线
		for (int i = 0; i < uids.size(); i++) {
			statusMap.put(uids.get(i), scores.get(i) != null);
		}
		return R.success(statusMap);
	}

	/**
	 * 批量查询群里面在线人员的数量
	 * @param roomIds
	 * @return 返回每个房间中的在线数量
	 */
	@PostMapping("/group/online-counts")
	public R<Map<Long, Long>> getBatchGroupOnlineCounts(@RequestBody List<Long> roomIds) {
		// 1. 使用缓存键构造键
		List<String> keys = roomIds.stream().map(id -> PresenceCacheKeyBuilder.onlineGroupMembersKey(id).getKey()).collect(Collectors.toList());

		// 2. 批量查询
		List<Long> counts = cachePlusOps.sMultiCard(keys);

		// 3. 大群组特殊处理
		Map<Long, Long> result = new HashMap<>();
		for (int i = 0; i < roomIds.size(); i++) {
			result.put(roomIds.get(i), counts.get(i));
		}
		return R.success(result);
	}

	/**
	 * 查询群组在线成员列表
	 *
	 * @param roomId 房间id
	 * @return 返回在线成员的uid
	 */
	@GetMapping("/group/{roomId}/online-members")
	public R<List<Long>> getGroupOnlineMembers(@PathVariable @NotNull Long roomId) {
		// 1. 构建缓存键
		CacheKey cacheKey = PresenceCacheKeyBuilder.onlineGroupMembersKey(roomId);

		// 2. 获取总在线人数
		Long total = cachePlusOps.sSize(cacheKey.getKey());
		if (total == null || total == 0) {
			return R.success(new ArrayList<>());
		}

		// 4. 查询在线成员
		return R.success(cachePlusOps.sMembers(cacheKey).stream().map(obj -> Long.parseLong(obj.toString())).collect(Collectors.toList()));
	}
}
