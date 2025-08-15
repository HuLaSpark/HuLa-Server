package com.luohuo.flex.model.entity.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSLoginSuccess implements Serializable {
    private Long uid;
    private String avatar;
    private String token;
	private String refreshToken;
    private String name;
    //用户权限 0普通用户 1超管
    private Integer power;
}
