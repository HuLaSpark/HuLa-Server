package com.luohuo.basic.boot.utils;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.List;

public final class TokenCleanupUtils {
    private TokenCleanupUtils() {}

    public static void clearSessionRefreshTokens(SaSession sess) {
        if (sess == null) {
            return;
        }
        try {
            Object rtObj = sess.get("refreshTokens");
            if (rtObj instanceof java.util.List) {
                for (Object rto : (java.util.List<?>) rtObj) {
                    if (rto != null) {
                        SaTempUtil.deleteToken(rto.toString());
                    }
                }
                sess.delete("refreshTokens");
            }
        } catch (Exception ignored) {}
    }

    public static void deleteTempTokensByLoginId(Object loginId) {
        if (loginId == null) {
            return;
        }
        try {
            List<String> keys = SaManager.getSaTokenDao().searchData("Token:temp-token:", "", 0, -1, false);
            for (String key : keys) {
                Object val = SaManager.getSaTokenDao().get(key);
                if (val != null) {
                    JSONObject obj = JSONUtil.parseObj(val.toString());
                    Long kUserId = obj.getLong("userId");
                    if (kUserId != null && kUserId.toString().equals(String.valueOf(loginId))) {
                        String rt = StrUtil.subAfter(key, "Token:temp-token:", true);
                        SaTempUtil.deleteToken(rt);
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}
