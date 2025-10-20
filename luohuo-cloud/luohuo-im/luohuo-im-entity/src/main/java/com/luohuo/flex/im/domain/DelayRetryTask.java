package com.luohuo.flex.im.domain;

import com.luohuo.flex.model.entity.WsBaseResp;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DelayRetryTask {
	// 原始消息
    private WsBaseResp<?> originalMsg;
	// 目标用户
    private List<Long> targetUids;
	// 操作者
    private Long operatorUid;
	// 哈希值
	private Long hashId;
	// 重试次数
    private Integer retryCount;
    private String retryReason;
}