package com.luohuo.flex.im.api;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.luohuo.basic.base.R;
import com.luohuo.basic.constant.Constants;
import com.luohuo.flex.im.api.hystrix.PresenceApiFallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户
 *
 * @author 乾乾
 * @date 2025/07/28
 */
@FeignClient(name = "${" + Constants.PROJECT_PREFIX + ".feign.tenant-server:luohuo-presence-server}", fallback = PresenceApiFallback.class)
public interface PresenceApi {
	/**
	 * 查询用户的在线id集合
	 *
	 * @return 用户id
	 */
	@PostMapping("/online/user/online-users-list")
	R<Set<Long>> getOnlineUsersList(@RequestBody List<Long> uids);

	/**
	 * 查询用户的在线状态
	 *
	 * @return 用户id
	 */
	@PostMapping("/online/user/online-status")
	R<Map<Long, Boolean>> getUsersOnlineStatus(@RequestBody List<Long> uids);

    /**
     * 查询房间所有的在线成员
     *
     * @return 用户id
     */
	@GetMapping("/online/group/{roomId}/online-members")
	R<List<Long>> getGroupOnlineMembers(@PathVariable @NotNull Long roomId);

	/**
	 * 查询所有房间里面的在线人数
	 *
	 * @return 房间id与在线人数的映射
	 */
	@PostMapping("/online/group/online-counts")
	R<Map<Long, Long>> getBatchGroupOnlineCounts(@RequestBody List<Long> roomIds);
}
