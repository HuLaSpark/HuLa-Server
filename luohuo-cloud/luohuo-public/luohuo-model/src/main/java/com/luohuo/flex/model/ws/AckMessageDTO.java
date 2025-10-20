package com.luohuo.flex.model.ws;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AckMessageDTO implements Serializable {
    /**
     * 消息id
     */
    private Long msgId;

	/**
	 * 时间戳
	 */
	private Long timestamp;

    /**
     * 回执的uid
     */
    private Long uid;
}