package com.hula.ai.common.utils;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Java Bean 转换工具类
 * 默认转换类
 *
 * @author: 云裂痕
 * @date: 2020/12/24
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class DozerUtil {
    /**
     * 持有Dozer单例, 避免重复创建DozerMapper消耗资源.
     */
    private static Mapper MAPPER = DozerBeanMapperBuilder.buildDefault();

    public static void init(Mapper mapper) {
        DozerUtil.MAPPER = mapper;
    }

    /**
     * List  实体类 转换器
     *
     * @param source 原数据
     * @param clz    转换类型
     * @param <T>
     * @return
     */
    public static <T, S> List<T> convertor(List<S> source, Class<T> clz) {
        if (source == null) {
            return null;
        }
        List<T> list = new ArrayList<>();
        for (S s : source) {
            list.add(MAPPER.map(s, clz));
        }
        return list;
    }

    /**
     * List  实体类 深度转换器
     *
     * @param source 原数据
     * @param clz    转换类型
     * @param mapId  自定义转换
     * @return
     */
    public static <T, S> List<T> convertor(List<S> source, Class<T> clz, String mapId) {
        if (source == null) {
            return null;
        }
        List<T> list = new ArrayList<>();
        for (S s : source) {
            list.add(MAPPER.map(s, clz, mapId));
        }
        return list;
    }


    /**
     * Set 实体类 转换器
     *
     * @param source 原数据
     * @param clz    目标对象
     * @return
     */
    public static <T, S> Set<T> convertor(Set<S> source, Class<T> clz) {
        if (source == null) {
            return null;
        }
        Set<T> set = new TreeSet<>();
        for (S s : source) {
            set.add(MAPPER.map(s, clz));
        }
        return set;
    }

    /**
     * Set 实体类 深度转换器
     *
     * @param source 原数据
     * @param clz    目标对象
     * @param mapId  自定义转换
     * @return
     */
    public static <T, S> Set<T> convertor(Set<S> source, Class<T> clz, String mapId) {
        if (source == null) {
            return null;
        }
        Set<T> set = new TreeSet<>();
        for (S s : source) {
            set.add(MAPPER.map(s, clz, mapId));
        }
        return set;
    }

    /**
     * 实体类 转换器
     *
     * @param source 原数据
     * @param clz    目标对象
     * @return
     */
    public static <T, S> T convertor(S source, Class<T> clz) {
        if (source == null) {
            return null;
        }
        return MAPPER.map(source, clz);
    }

    /**
     * 实体类 深度转换器
     *
     * @param source 原数据
     * @param clz    目标对象
     * @param mapId  自定义转换
     * @return
     */
    public static <T, S> T convertor(S source, Class<T> clz, String mapId) {
        if (source == null) {
            return null;
        }
        return MAPPER.map(source, clz, mapId);
    }

    /**
     * 实体类复制
     *
     * @param source 原数据
     * @param object 目标对象
     * @return
     */
    public static void convertor(Object source, Object object) {
        MAPPER.map(source, object);
    }

    /**
     * 实体类深度复制
     *
     * @param source 原数据
     * @param object 目标对象
     * @param mapId 自定义转换
     * @return
     */
    public static void convertor(Object source, Object object, String mapId) {
        MAPPER.map(source, object, mapId);
    }

    /**
     * 实体类复制
     *
     * @param source 原数据
     * @param object 目标对象
     * @return
     */
    public static <T> void copyConvertor(T source, Object object) {
        MAPPER.map(source, object);
    }

    /**
     * 实体类深度复制
     *
     * @param source 原数据
     * @param object 目标对象
     * @param mapId 自定义转换
     * @return
     */
    public static <T> void copyConvertor(T source, Object object, String mapId) {
        MAPPER.map(source, object, mapId);
    }

}
