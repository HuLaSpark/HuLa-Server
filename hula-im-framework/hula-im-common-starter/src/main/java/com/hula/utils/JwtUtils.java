package com.hula.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Description: jwt的token生成与解析
 * @author nyh
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * token秘钥，请勿泄露，请勿随便修改
     */
    @Value("${HuLa-IM.JWT.SECRET}")
    private String secret;

    private static final String UID_CLAIM = "uid";
    private static final String LOGIN_TYPE_CLAIM = "loginType";
    private static final String CREATE_TIME = "createTime";

    /**
     * JWT生成Token
     * JWT构成: header, payload, signature
     *
     * @param uid 用户id
     * @return {@link String } token
     */
    public String createToken(Long uid, String loginType) {
        // build token
        return JWT.create()
                // 只存一个uid信息，其他的自己去redis查
                .withClaim(UID_CLAIM, uid)
                .withClaim(LOGIN_TYPE_CLAIM, loginType)
                .withClaim(CREATE_TIME, new Date())
                .withExpiresAt(DateUtil.addDays(new Date(), 7))
                // signature
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 解密Token
     *
     * @param token 令牌
     * @return {@link Map }<{@link String }, {@link Claim }>
     */
    public Map<String, Claim> verifyToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
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
    public Long getUidOrNull(String token) {
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
    public String getLoginType(String token) {
        return Optional.ofNullable(verifyToken(token))
                .map(map -> map.get(LOGIN_TYPE_CLAIM))
                .map(Claim::asString)
                .orElse(null);
    }


    public static void main(String[] args) {
        JwtUtils jwtUtils = new JwtUtils();
        String dsfsdfsdfsdfsd = JWT.create()
                // 只存一个uid信息，其他的自己去redis查
                .withClaim(UID_CLAIM, 20000)
                .withClaim(CREATE_TIME, new Date())
                // signature
                .sign(Algorithm.HMAC256("dsfsdfsdfsdfsd"));
        System.out.println("dsfsdfsdfsdfsd = " + JsonUtils.toStr(dsfsdfsdfsdfsd));

        String obj = JsonUtils.toObj(JsonUtils.toStr(dsfsdfsdfsdfsd), String.class);
        System.out.println("obj = " + obj);
    }

}
