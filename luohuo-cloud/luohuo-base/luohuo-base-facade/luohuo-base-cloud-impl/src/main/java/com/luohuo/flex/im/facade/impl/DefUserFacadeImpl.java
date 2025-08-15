package com.luohuo.flex.im.facade.impl;

import com.luohuo.flex.im.facade.DefUserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.luohuo.basic.base.R;
import com.luohuo.flex.model.constant.EchoApi;
import com.luohuo.flex.im.api.DefUserApi;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author tangyh
 * @since 2024/9/20 23:33
 */
@Service(EchoApi.DEF_USER_ID_CLASS)
@RequiredArgsConstructor
public class DefUserFacadeImpl implements DefUserFacade {
    @Autowired
    @Lazy  // 一定要延迟加载，否则luohuo-gateway-server无法启动
    private DefUserApi defUserApi;

    @Override
    public R<List<Long>> findAllUserId() {
        return defUserApi.findAllUserId();
    }

    @Override
    public Map<Serializable, Object> findByIds(Set<Serializable> ids) {
        return defUserApi.findByIds(ids);
    }

}

