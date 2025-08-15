package com.luohuo.flex.oauth.facde.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.luohuo.flex.model.constant.EchoApi;
import com.luohuo.flex.oauth.facade.DictFacade;
import com.luohuo.flex.oauth.service.DictService;

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
    private final DictService dictService;

    @Override
    public Map<Serializable, Object> findByIds(Set<Serializable> ids) {
        return dictService.findByIds(ids);
    }
}
