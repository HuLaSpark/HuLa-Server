package com.luohuo.flex.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 将扫码登录返回信息推送给所有横向扩展的服务
 * @author 乾乾
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginMessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long uid;
    private Integer code;

}
