package com.hula.core.user.domain.vo.resp.config;

import lombok.Data;

@Data
public class QiNiu {

	private String ossDomain;

	private String fragmentSize;

	// 超过多少容量开启分片
	private String turnSharSize;
}
