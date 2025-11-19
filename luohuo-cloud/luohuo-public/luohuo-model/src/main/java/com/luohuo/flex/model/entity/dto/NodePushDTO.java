package com.luohuo.flex.model.entity.dto;

import com.luohuo.flex.model.entity.WsBaseResp;
import jakarta.annotation.Nonnull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 指纹级别的精确路由
 * @author 乾乾
 */
@Data
@NoArgsConstructor
public class NodePushDTO implements Serializable {
    /**
     * 推送的ws消息
     */
    private WsBaseResp<?> wsBaseMsg;
    /**
     * 指纹与uid的映射
     */
	private Map<String, Long> deviceUserMap;
	/**
	 * 消息唯一hashId
	 */
	private Long hashId;
    /**
     * 操作人uid
     */
    private Long uid;

	public NodePushDTO(WsBaseResp<?> wsBaseMsg, Map<String, Long> deviceUserMap, @Nonnull Long hashId, @Nonnull Long uid) {
		this.wsBaseMsg = wsBaseMsg;
		this.deviceUserMap = deviceUserMap;
		this.hashId = hashId;
		this.uid = uid;
	}
}
