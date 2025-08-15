package com.luohuo.flex.model.ws;

import com.luohuo.flex.model.enums.CallStatusEnum;
import lombok.Data;
import java.io.Serializable;

/**
 * 音视频消息元数据
 */
@Data
public class CallEndReq implements Serializable {
	private Boolean begin = false;
	private Long uid;
    private Long roomId;
	private Long tenantId;
	private Boolean isGroup;
    private Boolean mediumType;
    private Long creator;
    private Long startTime;
    private Long endTime;
	/**
	 * 通话状态
	 * @see CallStatusEnum
	 */
	private String state;

	public CallEndReq(Long uid, Long roomId, Long tenantId, Boolean isGroup, Boolean mediumType, Long creator, Long startTime, Long endTime, String state) {
		this.uid = uid;
		this.roomId = roomId;
		this.tenantId = tenantId;
		this.isGroup = isGroup;
		this.mediumType = mediumType;
		this.creator = creator;
		this.startTime = startTime;
		this.endTime = endTime;
		this.state = state;
	}

	public CallEndReq(Long uid, Long creator, Long roomId, Long tenantId, Long startTime, String state) {
		this.begin = true;
		this.uid = uid;
		this.creator = creator;
		this.roomId = roomId;
		this.tenantId = tenantId;
		this.startTime = startTime;
		this.state = state;
		this.mediumType = false;
	}

	public CallEndReq() {}
}