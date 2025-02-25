package com.hula.core.user.domain.vo.resp.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户上线变动的推送类
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSOnlineNotify {
    //新的上下线用户
    private ChatMemberResp member = new ChatMemberResp();
    //在线人数
    private Long onlineNum;
}
