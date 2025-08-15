package com.luohuo.flex.ws.service;

import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.flex.common.cache.PresenceCacheKeyBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * IM在线人员服务 [单个群人员大于10万时需要开启redis分片存储、分片查询]
 * 重复实现了 luohuo-presence 里面的功能，因为不能用db、mvc等组件
 * @author 乾乾
 */
@Slf4j
@Tag(name = "在线服务")
@Service
@RequiredArgsConstructor
public class OnlineService {
	private final CachePlusOps cachePlusOps;

	/**
	 * 查询房间中所有的在线人员
	 * @param roomId 房间id
	 * @return
	 */
	public List<Long> getGroupOnlineMembers(@RequestBody Long roomId) {
		CacheKey cacheKey = PresenceCacheKeyBuilder.groupMembersKey(roomId);

		// 1. 获取总在线人数
		Long total = cachePlusOps.sSize(cacheKey.getKey());
		if (total == null || total == 0) {
			return new ArrayList<>();
		}

		// 2. 查询在线成员
		return cachePlusOps.sMembers(cacheKey).stream()
				.map(obj -> Long.parseLong(obj.toString()))
				.collect(Collectors.toList());
	}

}
