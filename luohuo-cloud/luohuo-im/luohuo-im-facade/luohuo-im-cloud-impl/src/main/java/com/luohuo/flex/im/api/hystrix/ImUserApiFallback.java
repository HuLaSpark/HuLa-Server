package com.luohuo.flex.im.api.hystrix;

import com.luohuo.basic.exception.BizException;
import com.luohuo.flex.im.api.vo.UserRegisterVo;
import com.luohuo.flex.im.domain.vo.resp.user.UserInfoResp;
import com.luohuo.flex.model.entity.base.RefreshIpInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.luohuo.basic.base.R;
import com.luohuo.flex.im.api.ImUserApi;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户API熔断
 *
 * @author 乾乾
 * @date 2019/07/23
 */
@Component
@RequiredArgsConstructor
public class ImUserApiFallback implements ImUserApi {

	@Override
	public R<Boolean> checkEmail(String email) {
		return R.success(true);
	}

	@Override
	public R<UserInfoResp> getById(Long id) {
		return R.success(new UserInfoResp());
	}

	@Override
	public R<Long> findById(@RequestParam("id") Long id, @RequestParam("tenantId") Long tenantId){
        throw BizException.wrap("ID: " + id + "用户不存在");
	}

    @Override
    public R<Boolean> register(UserRegisterVo userRegisterVo) {
        throw BizException.wrap("注册失败");
    }

	@Override
	public R<Boolean> refreshIpInfo(RefreshIpInfo refreshIpInfo) {
		return R.success(true);
	}
}
