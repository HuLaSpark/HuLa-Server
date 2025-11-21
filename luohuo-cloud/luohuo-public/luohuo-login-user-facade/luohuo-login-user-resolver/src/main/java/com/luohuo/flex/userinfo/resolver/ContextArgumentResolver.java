package com.luohuo.flex.userinfo.resolver;

import cn.hutool.core.util.BooleanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.luohuo.basic.annotation.user.LoginUser;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.flex.model.entity.system.SysUser;
import com.luohuo.flex.model.vo.result.UserQuery;
import com.luohuo.flex.userinfo.service.UserResolverService;

/**
 * Token转化SysUser
 *
 * @author 乾乾
 * @date 2018/12/21
 */
@Slf4j
public class ContextArgumentResolver implements HandlerMethodArgumentResolver {

    private UserResolverService userResolverService;

    /**
     * 入参筛选
     *
     * @param mp 参数集合
     * @return 格式化后的参数
     */
    @Override
    public boolean supportsParameter(MethodParameter mp) {
        return mp.hasParameterAnnotation(LoginUser.class) && mp.getParameterType().equals(SysUser.class);
    }

    private UserResolverService get() {
        UserResolverService urService;
        if (userResolverService == null) {
            urService = SpringUtils.getBean(UserResolverService.class);
            userResolverService = urService;
        } else {
            urService = userResolverService;
        }
        return urService;
    }

    /**
     * @param mp                    入参集合
     * @param modelAndViewContainer model 和 view
     * @param nativeWebRequest      web相关
     * @param webDataBinderFactory  入参解析
     * @return 包装对象
     */
    @Override
    public Object resolveArgument(@NonNull MethodParameter mp,
                                  ModelAndViewContainer modelAndViewContainer,
                                  @NonNull NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) {
        Long userId = ContextUtil.getUserId();
        Long uid = ContextUtil.getUid();
        //以下代码为 根据 @LoginUser 注解来注入 SysUser 对象
        SysUser user = SysUser.builder()
                .employeeId(uid)
                .build();
        user.setId(userId);
		// 如果非后台系统，那么直接返回用户的双id
        if (userId == null || "2".equals(ContextUtil.getSystemType())) {
            return user;
        }
        try {
            LoginUser loginUser = mp.getParameterAnnotation(LoginUser.class);
            if (loginUser == null) {
                return user;
            }

            boolean isQuery = BooleanUtil.or(loginUser.isFull(), loginUser.isEmployee(), loginUser.isPosition(), loginUser.isOrg(),
                    loginUser.isRoles(), loginUser.isResource(), loginUser.isUser());
            if (isQuery) {
                UserResolverService urService = get();

                R<SysUser> result = urService.getById(
                        UserQuery.builder()
                                .userId(userId)
                                .employeeId(uid)
                                .full(loginUser.isFull())
                                .org(loginUser.isOrg())
                                .currentOrg(loginUser.isMainOrg())
                                .user(loginUser.isUser())
                                .position(loginUser.isPosition())
                                .roles(loginUser.isRoles())
                                .resource(loginUser.isResource())
                                .employee(loginUser.isEmployee())
                                .build());
                if (result.getsuccess() && result.getData() != null) {
                    return result.getData();
                }
            }
        } catch (Exception e) {
            log.warn("注入登录人信息时，发生异常. --> {}", user, e);
        }
        return user;
    }

}
