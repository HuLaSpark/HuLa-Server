package com.luohuo.flex.model.entity.ws;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员变更数据传输对象
 * @author 乾乾
 */
@Data
public class AdminChangeDTO implements Serializable {
	/**
	 * 房间ID
	 */
	private String roomId;

	/**
	 * 管理员ID列表
	 */
	private List<String> uids;

	/**
	 * true: 设置  false: 取消
	 */
	private Boolean status;

	public AdminChangeDTO(Long roomId, List<Long> uids, Boolean status) {
		this.roomId = roomId != null ? roomId.toString() : null;
		this.uids = uids != null ? uids.stream().map(Object::toString).collect(Collectors.toList()) : null;
		this.status = status;
	}

	public AdminChangeDTO() {
	}
}