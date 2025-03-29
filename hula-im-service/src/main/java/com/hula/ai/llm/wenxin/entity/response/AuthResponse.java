package com.hula.ai.llm.wenxin.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文心一言token信息
 *
 * @author: 云裂痕
 * @date: 2024/5/30
 * @version: 1.1.7
 * Copyright Ⓒ 2024 Master Computer Corporation Limited All rights reserved.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    String refresh_token;
    Long expires_in;
    String session_key;
    String access_token;
    String scope;
    String session_secret;

}
