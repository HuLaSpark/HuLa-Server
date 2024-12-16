package com.hula.core.user.domain.vo.resp.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户下线变动的推送类
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WsTokenExpire {
    /**
     * 用户id
     */
    private Long uid;
    // 新IP
    private String ip;
}
