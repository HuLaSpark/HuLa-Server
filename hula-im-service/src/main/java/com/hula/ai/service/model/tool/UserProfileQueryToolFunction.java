package com.hula.ai.service.model.tool;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.hula.ai.utils.AiUtils;
import com.hula.ai.utils.BeanUtils;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

/**
 * 工具：当前用户信息查询
 *
 * 同时，也是展示 ToolContext 上下文的使用
 *
 */
@Component("user_profile_query")
public class UserProfileQueryToolFunction
        implements BiFunction<UserProfileQueryToolFunction.Request, ToolContext, UserProfileQueryToolFunction.Response> {

    @Resource
    private UserService userService;

    @Data
    @JsonClassDescription("当前用户信息查询")
    public static class Request { }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {

        /**
         * 用户ID
         */
        private Long id;
        /**
         * 用户昵称
         */
        private String nickname;

        /**
         * 手机号码
         */
        private String mobile;
        /**
         * 用户头像
         */
        private String avatar;

    }

    @Override
    public Response apply(Request request, ToolContext toolContext) {
        Long uid = (Long) toolContext.getContext().get(AiUtils.TOOL_CONTEXT_LOGIN_USER);
        User userById = userService.getUserById(uid);
        return BeanUtils.toBean(userById, Response.class);
    }

}
