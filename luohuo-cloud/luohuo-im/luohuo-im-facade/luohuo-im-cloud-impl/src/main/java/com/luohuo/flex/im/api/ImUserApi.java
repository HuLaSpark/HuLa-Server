package com.luohuo.flex.im.api;

import com.luohuo.basic.base.R;
import com.luohuo.flex.im.api.hystrix.ImUserApiFallback;
import com.luohuo.flex.im.api.vo.UserRegisterVo;
import com.luohuo.flex.im.domain.vo.resp.user.UserInfoResp;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户
 *
 * @author 乾乾
 * @date 2025/06/12
 */
@FeignClient(name = "luohuo-im-server",  fallback = ImUserApiFallback.class)
public interface ImUserApi {

	@GetMapping("/user/checkEmail")
	@Operation(summary ="校验邮箱是否存在 [仅远程接口调用]")
	R<Boolean> checkEmail(@RequestParam("email") String email);

	/**
	 * 获取前端展示信息
	 */
	@GetMapping("/user/getById/{id}")
	@Operation(summary ="用户详情 [仅远程接口调用]")
	R<UserInfoResp> getById(@PathVariable("id") Long id);

	/**
	 * 根据DefUserId查询im用户的id
	 * 远程传输租户信息
	 */
	@GetMapping("/user/findById")
	R<Long> findById(@RequestParam("id") Long id, @RequestParam("tenantId") Long tenantId);

	/**
	 * 注册用户
	 *
	 * @param userRegisterVo im系统中user表需要的数据
	 */
    @PostMapping("/user/register")
    R<Boolean> register(@Valid @RequestBody UserRegisterVo userRegisterVo);
}
