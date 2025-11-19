package com.luohuo.flex.oauth.granter;

import org.springframework.stereotype.Component;
import com.luohuo.basic.exception.BizException;
import com.luohuo.flex.oauth.enumeration.GrantType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TokenGranterBuilder
 * 采用策略模式，根据不同的登录策略，实现登录
 *
 * @author 乾乾
 * @date 2020年03月31日10:27:24
 */
@Component
public class TokenGranterBuilder {

    private final Map<String, TokenGranter> granterPool = new ConcurrentHashMap<>();

    public TokenGranterBuilder(Map<String, TokenGranter> granterPool) {
        /*
         * 为何启动时，granterPool有中3个数据？
         *
         * spring 有3种注入方式：
         *   1. 字段注入 如：@Autowired、@Resource
         *   2. set 方法注入
         *   3. 构造器注入
         * 这里用了构造器注入
         *
         * 除了支持注入单个实现类，还支持注入List、Map、数组等。
         */
        this.granterPool.putAll(granterPool);
    }

    /**
     * 获取TokenGranter
     *
     * @param grantType 授权类型
     * @return ITokenGranter
     */
    public TokenGranter getGranter(GrantType grantType) {
        if (grantType == null) {
            throw new BizException("请传递正确的 grantType 参数");
        }
        // 策略模式
        TokenGranter tokenGranter = granterPool.get(grantType.name());
        if (tokenGranter == null) {
            throw new BizException("grantType 不支持，请传递正确的 grantType 参数");
        }
        return tokenGranter;
    }

    public TokenGranter getGranter() {
        TokenGranter tokenGranter = granterPool.get(GrantType.PASSWORD.name());
        if (tokenGranter == null) {
            throw new BizException("grantType 不支持，请传递正确的 grantType 参数");
        }
        return tokenGranter;
    }

}
