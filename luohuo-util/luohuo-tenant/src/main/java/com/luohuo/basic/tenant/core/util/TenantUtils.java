package com.luohuo.basic.tenant.core.util;

import com.luohuo.basic.context.ContextUtil;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.luohuo.basic.context.ContextConstants.HEADER_TENANT_ID;


/**
 * 多租户 Util
 *
 * @author 乾乾
 */
public class TenantUtils {

    /**
     * 使用指定租户，执行对应的逻辑
     *
     * 注意，如果当前是忽略租户的情况下，会被强制设置成不忽略租户
     * 当然，执行完成后，还是会恢复回去
     *
     * @param tenantId 租户编号
     * @param runnable 逻辑
     */
    public static void execute(Long tenantId, Runnable runnable) {
        Long oldTenantId = ContextUtil.getTenantId();
        Boolean oldIgnore = ContextUtil.isIgnore();
        try {
            ContextUtil.setTenantId(tenantId);
            ContextUtil.setIgnore(false);
            // 执行逻辑
            runnable.run();
        } finally {
            ContextUtil.setTenantId(oldTenantId);
            ContextUtil.setIgnore(oldIgnore);
        }
    }

    /**
     * 使用指定租户，执行对应的逻辑
     *
     * 注意，如果当前是忽略租户的情况下，会被强制设置成不忽略租户
     * 当然，执行完成后，还是会恢复回去
     *
     * @param tenantId 租户编号
     * @param callable 逻辑
     * @return 结果
     */
    public static <V> V execute(Long tenantId, Callable<V> callable) {
        Long oldTenantId = ContextUtil.getTenantId();
        Boolean oldIgnore = ContextUtil.isIgnore();
        try {
            ContextUtil.setTenantId(tenantId);
            ContextUtil.setIgnore(false);
            // 执行逻辑
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ContextUtil.setTenantId(oldTenantId);
            ContextUtil.setIgnore(oldIgnore);
        }
    }

    /**
     * 忽略租户，执行对应的逻辑
     *
     * @param runnable 逻辑
     */
    public static void executeIgnore(Runnable runnable) {
        Boolean oldIgnore = ContextUtil.isIgnore();
        try {
            ContextUtil.setIgnore(true);
            // 执行逻辑
            runnable.run();
        } finally {
            ContextUtil.setIgnore(oldIgnore);
        }
    }

    /**
     * 忽略租户，执行对应的逻辑
     *
     * @param callable 逻辑
     * @return 结果
     */
    public static <V> V executeIgnore(Callable<V> callable) {
        Boolean oldIgnore = ContextUtil.isIgnore();
        try {
            ContextUtil.setIgnore(true);
            // 执行逻辑
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ContextUtil.setIgnore(oldIgnore);
        }
    }

    /**
     * 将多租户编号，添加到 header 中
     *
     * @param headers HTTP 请求 headers
     * @param tenantId 租户编号
     */
    public static void addTenantHeader(Map<String, String> headers, Long tenantId) {
        if (tenantId != null) {
            headers.put(HEADER_TENANT_ID, tenantId.toString());
        }
    }

}
