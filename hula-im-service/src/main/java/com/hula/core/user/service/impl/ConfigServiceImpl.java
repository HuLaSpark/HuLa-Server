package com.hula.core.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hula.common.constant.RedisKey;
import com.hula.core.user.domain.entity.Config;
import com.hula.core.user.domain.vo.req.config.ConfigParam;
import com.hula.core.user.domain.vo.resp.config.Init;
import com.hula.core.user.domain.vo.resp.config.QiNiu;
import com.hula.core.user.mapper.ConfigMapper;
import com.hula.core.user.service.ConfigService;
import com.hula.utils.RedisUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 系统配置 服务层实现
 * @author 乾乾
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

	private final ConfigMapper configMapper;

	/**
	 * 重置参数缓存数据
	 */
	public void resetConfigCache() {
		clearConfigCache();
		loadingConfigCache();
	}

	/**
	 * 清空参数缓存数据
	 */
	public void clearConfigCache() {
		RedisUtils.hdel(RedisKey.CONFIG_KEY);
	}

	/**
	 * 把数据同步到redis, 此方法适用于redis为空的时候进行一次批量输入
	 */
	public void loadingConfigCache() {
		Long size = RedisUtils.hLen(RedisKey.CONFIG_KEY);
		if (size > 0) {
			return;
		}
		init();
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
	public void deleteRedis(String key) {
		RedisUtils.hdel(RedisKey.CONFIG_KEY, key);
	}

	/**
	 * 走缓存获取键值
	 * @param name
	 * @return
	 */
	public String get(String name) {
		loadingConfigCache();
		String data = (String) RedisUtils.hget(RedisKey.CONFIG_KEY, name);
		if (ObjectUtil.isNull(data) || StrUtil.isBlank(data)) {
			// 没有找到数据
			return "";
		}
		// 去数据库查找，然后写入redis
		return data;
	}


	/**
	 * 把数据同步到redis
	 * @param configs List<Config> 需要同步的数据
	 */
	public void async(List<Config> configs) {
		for (Config config : configs) {
			RedisUtils.hset(RedisKey.CONFIG_KEY, config.getConfigKey(), config.getConfigValue());
		}
	}

	@Override
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
