package com.luohuo.flex.im.facae.impl;

import com.luohuo.flex.im.facade.DefUserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.luohuo.basic.base.R;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.model.constant.EchoApi;

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
    private final DefUserService defUserService;

    @Override
    public R<List<Long>> findAllUserId() {
        return R.success(defUserService.findUserIdList(null));
    }

    @Override
    public Map<Serializable, Object> findByIds(Set<Serializable> ids) {
        return defUserService.findByIds(ids);
    }

}
