package com.hula.utils;

import cn.hutool.extra.spring.SpringUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Description: jwt的token生成与解析
 * @author nyh
 */
@Slf4j
//Component
public class JwtUtils {

    /**
     * token秘钥，请勿泄露，请勿随便修改
     */
//    @Value("${HuLa-IM.JWT.SECRET}")
//    private String secret;
    private final static String SECRET_KEY = "HuLa-IM.JWT.SECRET";

    public static final String UID_CLAIM = "uid";
	public static final String LOGIN_TYPE_CLAIM = "loginType";
    private static final String CREATE_TIME = "createTime";

    /**
     * JWT生成Token
     * JWT构成: header, payload, signature
     *
     * @param uid 用户id
     * @return {@link String } token
     */
    public static String createToken(Long uid, String loginType, Integer day) {
        return JWT.create()
                .withClaim(UID_CLAIM, uid)
                .withClaim(LOGIN_TYPE_CLAIM, loginType)
                .withClaim(CREATE_TIME, new Date())
                // 过期时间
                .withExpiresAt(DateUtil.addDays(new Date(), day))
                // signature
                .sign(Algorithm.HMAC256(SpringUtil.getProperty(SECRET_KEY)));
    }

    /**
     * 解密Token
     *
     * @param token 令牌
     * @return {@link Map }<{@link String }, {@link Claim }>
     */
    public static Map<String, Claim> verifyToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SpringUtil.getProperty(SECRET_KEY))).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaims();
        } catch (Exception e) {
            log.error("decode error,token:{}", token, e);
        }
        return null;
    }


    /**
     * 根据Token获取uid
     *
     * @param token 令牌
     * @return {@link Long }
     */
    public static Long getUidOrNull(String token) {
        return Optional.ofNullable(verifyToken(token))
                .map(map -> map.get(UID_CLAIM))
                .map(Claim::asLong)
                .orElse(null);
    }

    /**
     * 根据Token获取uid
     *
     * @param token 令牌
     * @return {@link Long }
     */
    public static String getLoginType(String token) {
        return Optional.ofNullable(verifyToken(token))
                .map(map -> map.get(LOGIN_TYPE_CLAIM))
                .map(Claim::asString)
                .orElse(null);
    }


    public static void main(String[] args) {
        try {
            String token = JWT.create()
                    // 只存一个uid信息，其他的自己去redis查
                    .withClaim(UID_CLAIM, 20000)
                    .withClaim(CREATE_TIME, new Date())
                    .withExpiresAt(DateUtil.addMilliseconds(new Date(), 1))
                    // signature
                    .sign(Algorithm.HMAC256("dsfsdfsdfsdfsd"));
			Map<String, Claim> stringClaimMap = verifyToken(token);
			System.out.println(stringClaimMap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
