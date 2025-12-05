package com.luohuo.flex.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luohuo.flex.entity.IceServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.flex.common.cache.common.ConfigCacheKeyBuilder;
import com.luohuo.flex.entity.Config;
import com.luohuo.flex.entity.Init;
import com.luohuo.flex.entity.QiNiu;
import com.luohuo.flex.mapper.SysConfigMapper;
import com.luohuo.flex.service.SysConfigService;

import java.util.Arrays;
import java.util.List;

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

	/**
	 * 初始化配置
	 */
	@PostConstruct
	public void init() {
		async(configMapper.selectList(new QueryWrapper<>()));
		getSystemInit();
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

        // ICE Server
        var ice = new IceServer();
        String urlsRaw = get("iceServerUrls");
        java.util.List<String> urls;
        try {
            urls = JSON.parseArray(urlsRaw, String.class);
        } catch (Exception _e) {
            urls = Arrays.stream((urlsRaw == null ? "" : urlsRaw).split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
        ice.setUrls(urls);
        ice.setUsername(get("iceServerUsername"));
        ice.setCredential(get("iceServerCredential"));
        init.setIceServer(ice);

        return init;
    }
}
