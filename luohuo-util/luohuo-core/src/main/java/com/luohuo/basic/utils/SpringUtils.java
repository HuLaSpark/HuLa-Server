package com.luohuo.basic.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Spring工具类
 *
 * @author 乾乾
 * @date 2017-12-25 16:27
 */
public final class SpringUtils {
    private static ApplicationContext applicationContext;
    private static ApplicationContext parentApplicationContext;

    private SpringUtils() {
    }

    /**
     * 单例Holder模式： 优点：将懒加载和线程安全完美结合的一种方式（无锁）。（推荐）
     *
     * @return 实实例
     */
    public static SpringUtils getInstance() {
        return SpringUtilsHolder.INSTANCE;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext ctx) {
        Assert.notNull(ctx, "SpringUtil injection ApplicationContext is null");
        applicationContext = ctx;
        parentApplicationContext = ctx.getParent();
    }

    public static Object getBean(String name) {
        Assert.hasText(name, "SpringUtil name is null or empty");
        try {
            return applicationContext.getBean(name);
        } catch (Exception e) {
            return parentApplicationContext.getBean(name);
        }
    }

    public static <T> T getBean(String name, Class<T> type) {
        Assert.hasText(name, "SpringUtil name is null or empty");
        Assert.notNull(type, "SpringUtil type is null");
        try {
            return applicationContext.getBean(name, type);
        } catch (Exception e) {
            return parentApplicationContext.getBean(name, type);
        }
    }

    public static <T> T getBean(Class<T> type) {
        Assert.notNull(type, "SpringUtil type is null");
        try {
            return applicationContext.getBean(type);
        } catch (Exception e) {
            return parentApplicationContext.getBean(type);
        }
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        Assert.notNull(type, "SpringUtil type is null");
        try {
            return applicationContext.getBeansOfType(type);
        } catch (Exception e) {
            return parentApplicationContext.getBeansOfType(type);
        }
    }

    public static ApplicationContext publishEvent(Object event) {
        applicationContext.publishEvent(event);
        return applicationContext;
    }


    /**
     * <p>
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
     */
    private static class SpringUtilsHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static final SpringUtils INSTANCE = new SpringUtils();
    }

}
