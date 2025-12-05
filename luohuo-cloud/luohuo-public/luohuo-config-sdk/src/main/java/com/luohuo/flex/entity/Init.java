package com.luohuo.flex.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Init implements Serializable {

	// 系统名称
	private String name;
	// 系统logo
	private String logo;
	// 大群id
	private String roomGroupId;
	// 七牛云配置
	private QiNiu qiNiu;
	// ICE Server 配置
	private IceServer iceServer;
}
