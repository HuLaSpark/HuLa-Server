package com.luohuo.flex.im.common.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import com.luohuo.flex.im.domain.enums.BlackTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author nyh
 */
@Component
@RequiredArgsConstructor
public class BlackInterceptor implements HandlerInterceptor {

    private final UserSummaryCache userSummaryCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<Integer, Set<String>> blackMap = userSummaryCache.getBlackMap();
		Long uid = ContextUtil.getUid();
		if (isBlackList(uid, blackMap.get(BlackTypeEnum.UID.getType()))) {
			response.setStatus(HttpStatus.OK.value());
			R<Object> responseData = R.fail("对不起，不在白名单内");
			response.setContentType(ContentType.JSON.toString(Charset.forName("UTF-8")));
			response.getWriter().write(JSONUtil.toJsonStr(responseData));
            return false;
        }
        if (isBlackList(uid, blackMap.get(BlackTypeEnum.IP.getType()))) {
			response.setStatus(HttpStatus.OK.value());
			R<Object> responseData = R.fail("对不起，不在白名单内");
			response.setContentType(ContentType.JSON.toString(Charset.forName("UTF-8")));
			response.getWriter().write(JSONUtil.toJsonStr(responseData));
            return false;
        }
        return true;
    }

    private boolean isBlackList(Object target, Set<String> set) {
        if (Objects.isNull(target) || CollectionUtil.isEmpty(set)) {
            return false;
        }
        return set.contains(target.toString());
    }
}
