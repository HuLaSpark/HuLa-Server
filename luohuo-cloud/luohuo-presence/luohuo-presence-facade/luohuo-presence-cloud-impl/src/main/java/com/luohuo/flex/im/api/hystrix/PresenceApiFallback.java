package com.luohuo.flex.im.api.hystrix;

import org.springframework.stereotype.Component;
import com.luohuo.basic.base.R;
import com.luohuo.flex.im.api.PresenceApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 在线用户API熔断
 *
 * @author 乾乾
 * @date 2025/07/28
 */
@Component
public class PresenceApiFallback implements PresenceApi {
	@Override
	public R<Set<Long>> getOnlineUsersList(List<Long> uids) {
		return R.success(new HashSet<>());
	}

	@Override
	public R<Map<Long, Boolean>> getUsersOnlineStatus(List<Long> uids) {
		return R.success(new HashMap<>());
	}

	@Override
	public R<List<Long>> getGroupOnlineMembers(Long roomId) {
		return R.success(new ArrayList<>());
	}

	@Override
	public R<Map<Long, Long>> getBatchGroupOnlineCounts(List<Long> roomIds) {
		return R.success(new HashMap<>());
	}
}
