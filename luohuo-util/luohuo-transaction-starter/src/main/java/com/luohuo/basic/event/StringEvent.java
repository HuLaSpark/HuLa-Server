package com.luohuo.basic.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class StringEvent extends ApplicationEvent {
	public StringEvent(Object source) {
		super(source);
	}

	private Long key;

	private List<String> value;

	// 旧的orgId
	private Long oldOrg;

	// 旧orgId的新treepath
	private String newTreePath;

	private String topic;

	/**
	 * 针对部门跨片区移动的时候的远程mq的调整
	 * @param source
	 * @param topic
	 * @param value
	 * @param oldOrg
	 * @param newTreePath
	 * @param key
	 */
	public StringEvent(Object source, String topic, List<String> value, Long oldOrg, String newTreePath, Long key) {
		super(source);
		this.key = key;
		this.value = value;
		this.oldOrg = oldOrg;
		this.topic = topic;
		this.newTreePath = newTreePath;
	}
}
