package com.hula.core.user.domain.vo.resp.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户下线变动的推送类
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSOnlineNotify {
    //新的上下线用户
    private List<ChatMemberResp> changeList = new ArrayList<>();
    //在线人数
    private Long onlineNum;
}
