package com.luohuo.flex.ws.vo;

import lombok.Data;

@Data
public class MediaControlVO {
    private Long roomId;
    private boolean audioMuted;
    private boolean videoMuted;
}