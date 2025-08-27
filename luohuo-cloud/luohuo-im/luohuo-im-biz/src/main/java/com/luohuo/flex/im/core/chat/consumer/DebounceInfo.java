package com.luohuo.flex.im.core.chat.consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DebounceInfo {
    private Long lastMessageId;
    private Long lastUpdateTime;
    private int pendingCount;
}