package com.luohuo.flex.im.facae.impl;

import com.luohuo.flex.im.domain.vo.resp.user.UserInfoResp;
import com.luohuo.flex.im.facade.ImUserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.luohuo.flex.model.constant.EchoApi;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author 乾乾
 * @since 2025/6/12 23:33
 */
@Service(EchoApi.IM_USER_ID_CLASS)
@RequiredArgsConstructor
public class ImUserFacadeImpl implements ImUserFacade {

    @Override
    public UserInfoResp getUserInfo(Long id) {
        return new UserInfoResp();
    }

	@Override
	public Map<Serializable, Object> findByIds(Set<Serializable> ids) {
		// 要实现缓存查询
		return Map.of();
	}
}
