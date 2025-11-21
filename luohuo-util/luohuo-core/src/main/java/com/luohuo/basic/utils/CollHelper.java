package com.luohuo.basic.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.luohuo.basic.interfaces.BaseEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Map 类增强
 *
 * @author 乾乾
 * @date 2019/07/29
 */
public final class CollHelper {
    private CollHelper() {
    }

    public static Map<String, Set<String>> putAll(Map<String, Set<String>>... items) {
        if (ArrayUtil.isEmpty(items)) {
            return Collections.emptyMap();
        }
        Map<String, Set<String>> map = new HashMap<>();
        for (Map<String, Set<String>> item : items) {
            item.forEach((k, v) -> {
                if (map.containsKey(k)) {
                    Set<String> list = map.get(k);

                    if (list == null) {
                        list = new HashSet<>();
                    }
                    list.addAll(v);
                } else {
                    map.put(k, new HashSet<>(v));
                }
            });
        }
        return map;
    }

    /**
     * 将制定的枚举集合转成 map
     * key -> name
     * value -> desc
     *
     * @param list
     * @return
     */
    public static Map<String, String> getMap(BaseEnum[] list) {
        return CollHelper.uniqueIndex(Arrays.asList(list), BaseEnum::getCode, BaseEnum::getDesc);
    }

    /**
     * 增强 guava 的 Maps.uniqueIndex方法
     * <p>
     * guava 的 Maps.uniqueIndex方法可以实现：
     * <br>
     * 将 list&lt;V&gt 转成 Map&lt;K , V&gt
     * K 需要自己指定， V不能指定
     * </p>
     * <p>
     * 本方法实现了：
     * <p>
     * 将 {@code list<V> } 转成 {@code Map<K , M>}
     * K 需要自己指定， M需要自己指定
     * <p>
     * 其中K不能重复，若重复，则会报错
     * </p>
     *
     * @param values        需要转换的集合 可以是任何实现了 Iterable 接口的集合(如List, Set, Collection)
     * @param keyFunction   转换后Map的键的转换方式
     * @param valueFunction 转换后Map的值的转换方式
     * @param <K>           转换后Map的键 类型
     * @param <V>           转换前Iterable的迭代类型
     * @param <M>           转换后Map的值 类型
     * @return 唯一的map
     */
    public static <K, V, M> ImmutableMap<K, M> uniqueIndex(Iterable<V> values, Function<? super V, K> keyFunction, Function<? super V, M> valueFunction) {
        Iterator<V> iterator = values.iterator();
        checkNotNull(keyFunction);
        checkNotNull(valueFunction);
        ImmutableMap.Builder<K, M> builder = ImmutableMap.builder();
        while (iterator.hasNext()) {
            V value = iterator.next();
            builder.put(keyFunction.apply(value), valueFunction.apply(value));
        }
        try {
            return builder.build();
        } catch (IllegalArgumentException duplicateKeys) {
            throw new IllegalArgumentException(
                    duplicateKeys.getMessage()
                    + ".若要在键下索引多个值，请使用: Multimaps.index.", duplicateKeys);
        }
    }

    /**
     * 一个key 对应多个值的map
     * 结构： key -> [value1, value2, ...]
     *
     * @param values        需要转换的集合 可以是任何实现了 Iterable 接口的集合(如List, Set, Collection)
     * @param keyFunction   转换后Map的键的转换方式
     * @param valueFunction 转换后Map的值的转换方式
     * @param <K>           转换后Map的键 类型
     * @param <V>           转换前Iterable的迭代类型
     * @param <M>           转换后Map的值 类型
     * @return 唯一的map
     */
    public static <K, V, M> Multimap<K, M> iterableToMultiMap(Iterable<V> values, Function<? super V, K> keyFunction, Function<? super V, M> valueFunction) {
        Iterator<V> iterator = values.iterator();
        checkNotNull(keyFunction);
        checkNotNull(valueFunction);

        Multimap<K, M> builder = ArrayListMultimap.create();
        while (iterator.hasNext()) {
            V value = iterator.next();
            builder.put(keyFunction.apply(value), valueFunction.apply(value));
        }
        try {
            return builder;
        } catch (IllegalArgumentException duplicateKeys) {
            throw new IllegalArgumentException(
                    duplicateKeys.getMessage()
                    + ".若要在键下索引多个值，请使用: Multimaps.index.", duplicateKeys);
        }
    }


    /**
     * 转换 Map 的 K 和 V
     *
     * @param map map
     * @return 反转的map
     */
    public static <K, V> Map<V, K> inverse(Map<K, V> map) {
        if (MapUtil.isEmpty(map)) {
            return Collections.emptyMap();
        }
        HashBiMap<K, V> biMap = HashBiMap.create();
        map.forEach(biMap::forcePut);
        return biMap.inverse();
    }

    /**
     * 计算map 初始容量
     *
     * @param size       已知数量
     * @param loadFactor 加载因子
     * @return map 初始容量
     */
    public static int initialCapacity(int size, float loadFactor) {
        return (int) (size / loadFactor + 1);
    }

    /**
     * 计算map 初始容量
     *
     * @param size 已知数量
     * @return map 初始容量
     */
    public static int initialCapacity(int size) {
        return initialCapacity(size, 0.75F);
    }


    public static int computeListCapacity(int arraySize) {
        return (int) Math.min(5L + arraySize + (arraySize / 10), Integer.MAX_VALUE);
    }

    /**
     * 按照分隔符切割list
     *
     * @param list      集合
     * @param function  转换器
     * @param separator 分隔符
     * @return 分割后的集合
     */
    public static <T> List<String> split(Collection<T> list, Function<? super T, ?> function, CharSequence separator) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.parallelStream().map(function).map(item -> StrUtil.splitToArray(String.valueOf(item), separator))
                .flatMap(Arrays::stream).filter(ObjectUtil::isNotEmpty).distinct().toList();
    }

    /**
     * 按照分隔符切割list
     *
     * @param list      集合
     * @param separator 分隔符
     * @return 分割后的集合
     */
    public static <T> List<String> split(Collection<String> list, CharSequence separator) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.parallelStream().map(item -> StrUtil.splitToArray(item, separator))
                .flatMap(Arrays::stream).filter(ObjectUtil::isNotEmpty).distinct().toList();
    }


    public static <E> List<E> asList(E... elements) {
        if (elements == null || elements.length == 0) {
            return Collections.emptyList();
        }
        // Avoid integer overflow when a large array is passed in
        int capacity = computeListCapacity(elements.length);
        ArrayList<E> list = new ArrayList<>(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    public static <E> Set<E> asSet(E... elements) {
        if (elements == null || elements.length == 0) {
            return Collections.emptySet();
        }
        LinkedHashSet<E> set = new LinkedHashSet<>(elements.length * 4 / 3 + 1);
        Collections.addAll(set, elements);
        return set;
    }


    /**
     * 添加 多个List
     *
     * @param values 集合
     * @param <T>
     * @return
     */
    public static <T> List<T> addAll(List<T>... values) {
        return Stream.of(values).flatMap(List::stream).toList();
    }

    /**
     * 添加 多个List 并去重
     *
     * @param values 集合
     * @param <T>
     * @return
     */
    public static <T> List<T> addAllUnique(List<T>... values) {
        return Stream.of(values).flatMap(List::stream).distinct().toList();
    }

    /**
     * 添加 多个Set
     *
     * @param values 集合
     * @param <T>
     * @return
     */
    public static <T> Set<T> addAll(Set<T>... values) {
        return Stream.of(values).flatMap(Set::stream).collect(Collectors.toSet());
    }

    /**
     * 添加 多个List 并返回Set
     *
     * @param values 集合
     * @param <T>
     * @return
     */
    public static <T> Set<T> addAllListToSet(List<T>... values) {
        return Stream.of(values).flatMap(List::stream).collect(Collectors.toSet());
    }
}
