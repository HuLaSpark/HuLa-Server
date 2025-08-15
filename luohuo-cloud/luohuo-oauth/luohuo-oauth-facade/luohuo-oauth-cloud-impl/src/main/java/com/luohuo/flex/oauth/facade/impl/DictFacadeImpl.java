package com.luohuo.flex.oauth.facade.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.luohuo.flex.model.constant.EchoApi;
import com.luohuo.flex.oauth.api.DictApi;
import com.luohuo.flex.oauth.facade.DictFacade;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 字典实现
 * @author tangyh
 * @since 2024/9/20 23:29
 */
@Service(EchoApi.DICTIONARY_ITEM_FEIGN_CLASS)
@RequiredArgsConstructor
public class DictFacadeImpl implements DictFacade {
    @Autowired
    @Lazy  // 一定要延迟加载，否则luohuo-gateway-server无法启动
    private DictApi dictApi;

    @Override
    public Map<Serializable, Object> findByIds(Set<Serializable> ids) {
        return dictApi.findByIds(ids);
    }
}
