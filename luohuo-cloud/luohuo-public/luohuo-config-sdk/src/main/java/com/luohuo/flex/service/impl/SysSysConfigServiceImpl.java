package com.luohuo.flex.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.flex.common.cache.common.ConfigCacheKeyBuilder;
import com.luohuo.flex.entity.Config;
import com.luohuo.flex.entity.Init;
import com.luohuo.flex.entity.QiNiu;
import com.luohuo.flex.mapper.SysConfigMapper;
import com.luohuo.flex.service.SysConfigService;
import com.luohuo.flex.vo.config.ConfigParam;

import java.util.List;
import java.util.function.Function;

/**
 * 参数配置 服务层实现
 * @DataSource(DataSourceType.MASTER)
 * @author 乾乾
 */
@Service
@RequiredArgsConstructor
public class SysSysConfigServiceImpl implements SysConfigService {

	private final SysConfigMapper configMapper;
	private final CachePlusOps cachePlusOps;

	/**
	 * 重置参数缓存数据
	 */
//	@PostConstruct
	public void resetConfigCache() {
		clearConfigCache();
		init();
	}

	/**
	 * 清空参数缓存数据
	 */
	public void clearConfigCache() {
		cachePlusOps.del(ConfigCacheKeyBuilder.build(""));
	}

	/**
	 * 把数据同步到redis, 此方法适用于redis为空的时候进行一次批量输入
	 */
	public void loadingConfigCache() {
		Long size = cachePlusOps.hLen(ConfigCacheKeyBuilder.build(""));
		if (size > 0) {
			return;
		}
		init();
	}

	public <T> T indexInit(String key, Function<CacheHashKey, ? extends T> loader) {
		return cachePlusOps.hGet(ConfigCacheKeyBuilder.build(key), loader).getValue();
	}

	/**
	 * 初始化配置
	 */
	@PostConstruct
	public void init() {
		async(configMapper.selectList(new QueryWrapper<>()));
		getSystemInit();
	}

	/**
	 * 把数据同步到redis
	 */
	private void deleteRedis(String name) {
		cachePlusOps.hDel(ConfigCacheKeyBuilder.build(name));
	}

	/**
	 * 缓存转Bean对象
	 * @param name
	 * @param <T> 返回的类型
	 * @return
	 */
	public <T> T getBeanByName(String name, Class<T> t) {
		String data = get(name);
		if (StrUtil.isEmpty(data)) {
			return null;
		}
		return JSON.parseObject(data, t);
	}

	/**
	 * 走缓存获取键值
	 * @param name
	 * @return
	 */
	public String get(String name) {
		loadingConfigCache();
		CacheResult<String> result = cachePlusOps.hGet(ConfigCacheKeyBuilder.build(name));
		String data = result.getValue();
		if (ObjectUtil.isNull(data) || StrUtil.isBlank(data)) {
			// 没有找到数据
			return "";
		}
		// 去数据库查找，然后写入redis
		return result.getValue();
	}

	public void async(List<Config> configs) {
		for (Config config : configs) {
			cachePlusOps.hSet(ConfigCacheKeyBuilder.build(config.getConfigKey()), config.getConfigValue());
		}
	}

	public Long getLong(String name) {
		return Long.parseLong(get(name));
	}

	public Integer getInteger(String name) {
		return Integer.parseInt(get(name));
	}

	/**
	 * 根据 name 获取 value 找不到抛异常
	 * @param name menu name
	 * @return String
	 */
	public String getValueByKeyException(String name) {
		String value = get(name);
		if (null == value) {
			throw new BizException("没有找到"+ name +"数据");
		}

		return value;
	}

	/**
	 * 获取验证码开关
	 *
	 * @return true开启，false关闭
	 */
	public boolean selectCaptchaEnabled() {
		String captchaEnabled = get("captchaEnabled");
		if (StrUtil.isEmpty(captchaEnabled)) {
			return true;
		}
		return Convert.toBool(captchaEnabled);
	}

	public Init getSystemInit() {
		Init init = new Init();
		init.setLogo(get("logo"));
		init.setName(get("systemName"));
		init.setRoomGroupId(get("roomGroupId"));

		QiNiu qiNiu = new QiNiu();
		qiNiu.setOssDomain(get("qnStorageCDN"));
		qiNiu.setFragmentSize(get("fragmentSize"));
		qiNiu.setTurnSharSize(get("turnSharSize"));
		init.setQiNiu(qiNiu);
		return init;
	}

	public List<Config> selectConfigList(ConfigParam params) {
		return configMapper.selectList(new QueryWrapper<Config>()
				.eq("type", params.getType())
				.like("config_name", params.getConfigName())
				.like("config_key", params.getConfigKey()));
	}

	public Config selectConfigByConfigName(Config config) {
		return configMapper.selectOne(new QueryWrapper<>(Config.class)
				.eq("config_key", config.getConfigKey())
				.eq("type", config.getType())
				.last(" limit 1"));
	}
}
