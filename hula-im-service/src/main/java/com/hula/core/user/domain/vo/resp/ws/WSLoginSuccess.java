package com.hula.core.user.domain.vo.resp.ws;

import com.hula.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSLoginSuccess extends BaseEntity {
    private Long uid;
    private String avatar;
    private String token;
	private String refreshToken;
    private String name;
    //用户权限 0普通用户 1超管
    private Integer power;
}
