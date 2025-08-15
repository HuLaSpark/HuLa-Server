package com.luohuo.flex.im.domain.vo.res;

import lombok.Data;

/**
 * 群聊列表 VO
 *
 * @author Tian
 * @date 2024/12/31
 */
@Data
public class GroupListVO {

    /**
     * 组 ID
     */
    private Long groupId;
    /**
     * 房间 ID
     */
    private Long roomId;
    /**
     * 房间名称
     */
    private String roomName;

    /**
     * 群头像
     */
    private String avatar;

}
