package com.luohuo.flex.model.entity.base;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户ip信息
 * @author 乾乾
 */
@Data
@NoArgsConstructor
public class RefreshIpInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    //更新人id
    private Long uid;
    //ip信息
    private IpInfo ipInfo;

	public RefreshIpInfo(Long uid, IpInfo ipInfo) {
		this.uid = uid;
		this.ipInfo = ipInfo;
	}
}