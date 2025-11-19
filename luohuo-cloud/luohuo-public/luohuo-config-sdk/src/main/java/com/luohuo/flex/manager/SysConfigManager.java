package com.luohuo.flex.manager;

import com.luohuo.flex.entity.Config;
import com.luohuo.flex.mapper.SysConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.luohuo.basic.base.manager.SuperCacheManager;
import com.luohuo.basic.base.manager.impl.SuperCacheManagerImpl;
import com.luohuo.basic.interfaces.echo.LoadService;
import com.luohuo.basic.model.cache.CacheKeyBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 系统配置
 *
 * @author 乾乾
 * @date 2024-04-18
 */
@RequiredArgsConstructor
@Service
public class SysConfigManager extends SuperCacheManagerImpl<SysConfigMapper, Config>
		implements SuperCacheManager<Config>, LoadService {

	@Override
	public Map<Serializable, Object> findByIds(Set<Serializable> ids) {
		return null;
	}

	@Override
	protected CacheKeyBuilder cacheKeyBuilder() {
		return null;
	}
}
