package com.luohuo.basic.utils;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

/**
 * @author tangyh
 * @version v1.0
 * @date 2021/7/21 6:05 下午
 * @create [2021/7/21 6:05 下午 ] [tangyh] [初始创建]
 */
@Slf4j
public class ClassUtils {

    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

    private static Set<Class<?>> scanClasses(String packagePatterns, Class<?> assignableType) throws IOException {
        Set<Class<?>> classes = new HashSet<>();
        String[] packagePatternArray = tokenizeToStringArray(packagePatterns,
                ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        for (String packagePattern : packagePatternArray) {
            Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils.convertClassNameToResourcePath(packagePattern) + "/**/*.class");
            for (Resource resource : resources) {
                try {
                    ClassMetadata classMetadata = METADATA_READER_FACTORY.getMetadataReader(resource).getClassMetadata();
                    Class<?> clazz = Resources.classForName(classMetadata.getClassName());
                    if (assignableType == null || assignableType.isAssignableFrom(clazz)) {
                        classes.add(clazz);
                    }
                } catch (Throwable e) {
                    log.warn("Cannot load the '{}'. Cause by ", resource, e);
                }
            }
        }
        return classes;
    }

    /**
     * 扫面包路径下满足class过滤器条件的所有class文件，<br>
     * 如果包路径为 com.abs + A.class 但是输入 abs会产生classNotFoundException<br>
     * 因为className 应该为 com.abs.A 现在却成为abs.A,此工具类对该异常进行忽略处理,有可能是一个不完善的地方，以后需要进行修改<br>
     *
     * @param packageName 包路径 com.abs | com.abs.* | com.abs,com.xxx;com.ddd
     * @param classFilter class过滤器，过滤掉不需要的class
     * @return 类集合
     */
    @SneakyThrows
    public static Set<Class<?>> scanPackage(String packageName, Predicate<Class<?>> classFilter) {
        if (StringUtils.isBlank(packageName)) {
            return Collections.emptySet();
        }
        Set<Class<?>> classes;

        if (packageName.contains(StringPool.STAR) && !packageName.contains(StringPool.COMMA)
                && !packageName.contains(StringPool.SEMICOLON)) {
            classes = scanClasses(packageName, null);
        } else {
            classes = new HashSet<>();
            String[] packageNameArray = tokenizeToStringArray(packageName, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            Assert.notNull(packageNameArray, "not find packageName:" + packageName);
            Stream.of(packageNameArray).forEach(typePackage -> {
                try {
                    Set<Class<?>> scanTypePackage = scanClasses(typePackage, null);
                    classes.addAll(scanTypePackage);
                } catch (IOException e) {
                    throw new MybatisPlusException("Cannot scan class in '[" + typePackage + "]' package", e);
                }
            });
        }
        return classes.stream().filter(classFilter).collect(Collectors.toSet());
    }

}
