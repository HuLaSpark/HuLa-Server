package com.luohuo.basic.scan.utils;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.luohuo.basic.scan.model.SystemApiVO;
import com.luohuo.basic.utils.StrPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RequestMapping 扫描工具类
 *
 * @author 乾乾
 * @date 2019/12/16
 */
@Getter
public class RequestMappingScanUtils {

    private final Map<String, List<SystemApiVO>> requestMappingMap = new ConcurrentHashMap<>(512);

    private static boolean contains(String[] scanBaseArray, String curPackage) {
        return Arrays.stream(scanBaseArray).anyMatch(sb -> sb.equals(curPackage) || curPackage.startsWith(sb));
    }

    private static String getMethods(Set<RequestMethod> requestMethods) {
        StringBuilder sbf = new StringBuilder();
        for (RequestMethod requestMethod : requestMethods) {
            sbf.append(requestMethod.toString()).append(StrPool.COMMA);
        }
        if (!requestMethods.isEmpty()) {
            sbf.deleteCharAt(sbf.length() - 1);
        } else {
            sbf.append("ALL");
        }
        return sbf.toString();
    }

    private static String getUris(Set<String> urls) {
        StringBuilder sbf = new StringBuilder();
        for (String url : urls) {
            sbf.append(url).append(StrPool.COMMA);
        }
        if (!urls.isEmpty()) {
            sbf.deleteCharAt(sbf.length() - 1);
        }
        return sbf.toString();
    }

    public void scan(String scanBase, ApplicationContext applicationContext) {
        if (StrUtil.isEmpty(scanBase)) {
            return;
        }
        Environment env = applicationContext.getEnvironment();
        // 服务名称
        String springApplicationName = env.getProperty("spring.application.name", "base");
        // 网关请求路径前缀
        String servicePath = env.getProperty("spring.application.path", "");
        RequestMappingHandlerMapping mapping = (RequestMappingHandlerMapping) applicationContext.getBean("requestMappingHandlerMapping");

        String[] scanBaseArray = scanBase.split(StrPool.COMMA);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            String curPackage = method.getMethod().getDeclaringClass().getPackage().getName();
            if (!contains(scanBaseArray, curPackage)) {
                continue;
            }
            if (method.getMethodAnnotation(Hidden.class) != null) {
                // 忽略的接口不扫描
                continue;
            }

            // 请求类型
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            String requestMethod = getMethods(methodsCondition.getMethods());
            // 请求路径
            PatternsRequestCondition p = info.getPatternsCondition();
            assert p != null;
            String uri = getUris(p.getPatterns());
            /*PathPatternsRequestCondition pathPatternsCondition = info.getPathPatternsCondition();
            String uri = getUris(pathPatternsCondition.getPatternValues());*/
            // 类名
            String controller = method.getBeanType().getSimpleName();
            // 方法名
            String methodName = method.getMethod().getName();
            String name = "";

            Tag classApi = method.getBeanType().getAnnotation(Tag.class);
            if (classApi != null) {
                name = classApi.name();
            }

            Operation apiOperation = method.getMethodAnnotation(Operation.class);
            if (apiOperation != null) {
                name += (StrUtil.isNotEmpty(apiOperation.summary()) ? apiOperation.summary() : apiOperation.description());
            } else {
                name += methodName;
            }

            SystemApiVO api = SystemApiVO.builder()
                    .name(name).requestMethod(requestMethod)
                    .springApplicationName(springApplicationName).uri(servicePath + uri)
                    .controller(controller).build();

            if (requestMappingMap.containsKey(controller)) {
                requestMappingMap.get(controller).add(api);
            } else {
                List<SystemApiVO> list = new ArrayList<>();
                list.add(api);
                requestMappingMap.put(controller, list);
            }
        }
    }
}
