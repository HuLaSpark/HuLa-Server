package com.luohuo.flex.service;

import com.luohuo.flex.entity.Config;
import com.luohuo.flex.entity.Init;

import java.util.List;

/**
 * 参数配置 服务层实现
 * @author 乾乾
 */
public interface SysConfigService {

	/**
	 * 清空参数缓存数据
	 */
	void clearConfigCache();

	/**
	 * 把数据同步到redis, 此方法适用于redis为空的时候进行一次批量输入
	 */
	void loadingConfigCache();

	/**
	 * 初始化配置
	 */
	void init();

	/**
	 * 走缓存获取键值
	 * @param name
	 * @return
	 */
	String get(String name);

	/**
	 * 把数据同步到redis
	 * @param configs List<Config> 需要同步的数据
	 */
	void async(List<Config> configs);

	/**
	 * 获取系统全局配置
	 */
	Init getSystemInit();
}
