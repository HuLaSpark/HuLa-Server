package com.luohuo.flex.common.utils;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.utils.StrPool;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.luohuo.basic.exception.code.ResponseEnum.JWT_BASIC_INVALID;

/**
 * 工具类
 *
 * @author 乾乾
 */
public class Base64Util {


    /**
     * authorization: base64(clientId:clientSec)
     * 解析请求头中存储的 client 信息
     * <p>
     * Basic clientId:clientSec -截取-> clientId:clientSec后调用 extractAuthorization 解码
     *
     * @param basicHeader Basic clientId:clientSec
     * @return clientId:clientSec
     */
    public static String[] getAuthorization(String basicHeader) {
        if (StrUtil.isEmpty(basicHeader)) {
            throw BizException.wrap("客户端参数尚未传递");
        }

        return extractAuthorization(basicHeader);
    }

    /**
     * 解析请求头中存储的 authorization 信息
     * clientId:clientSec 解码
     */
    public static String[] extractAuthorization(String authorization) {
        String token = base64Decoder(authorization);
        int index = token.indexOf(StrPool.COLON);
        if (index == -1) {
            throw BizException.wrap(JWT_BASIC_INVALID);
        } else {
            return new String[]{token.substring(0, index), token.substring(index + 1)};
        }
    }

    /**
     * 使用 Base64 解码
     *
     * @param val 参数
     * @return 解码后的值
     */
    @SneakyThrows
    public static String base64Decoder(String val) {
        byte[] decoded = Base64.getDecoder().decode(val.getBytes(StandardCharsets.UTF_8));
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
