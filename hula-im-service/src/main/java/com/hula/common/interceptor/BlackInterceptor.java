package com.hula.common.interceptor;

import cn.hutool.core.collection.CollectionUtil;
import com.hula.core.user.domain.enums.BlackTypeEnum;
import com.hula.core.user.service.cache.UserCache;
import com.hula.domain.dto.RequestInfo;
import com.hula.enums.HttpErrorEnum;
import com.hula.utils.RequestHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author nyh
 */
@Component
@RequiredArgsConstructor
public class BlackInterceptor implements HandlerInterceptor {

    private final UserCache userCache;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<Integer, Set<String>> blackMap = userCache.getBlackMap();
        RequestInfo requestInfo = RequestHolder.get();
        if (isBlackList(requestInfo.getUid(), blackMap.get(BlackTypeEnum.UID.getType()))) {
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
            return false;
        }
        if (isBlackList(requestInfo.getIp(), blackMap.get(BlackTypeEnum.IP.getType()))) {
            HttpErrorEnum.ACCESS_DENIED.sendHttpError(response);
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
