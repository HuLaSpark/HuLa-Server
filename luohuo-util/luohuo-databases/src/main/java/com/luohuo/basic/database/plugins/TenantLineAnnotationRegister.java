package com.luohuo.basic.database.plugins;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import com.luohuo.basic.annotation.database.TenantLine;
import com.luohuo.basic.utils.StrPool;

import java.lang.reflect.Method;

/**
 * @author tangyh
 * @version v1.0
 * @date 2022/8/24 9:19 PM
 * Comte [2022/8/24 9:19 PM ] [tangyh] [初始创建]
 */
@RequiredArgsConstructor
@Slf4j
public class TenantLineAnnotationRegister implements EnvironmentCapable, BeanPostProcessor {
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    private Environment environment;
    private ResourcePatternResolver resourcePatternResolver;
    @Value("${luohuo.scan.basePackage}")
    private String packages;
    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(basePackage));
    }

    @Override
    public final Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        return this.environment;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        MapperScan mapperScan = AnnotationUtils.findAnnotation(bean.getClass(), MapperScan.class);
        if (mapperScan == null) {
            return bean;
        }
        String[] basePackages = mapperScan.basePackages();
        if (ArrayUtil.isEmpty(basePackages)) {
            return bean;
        }

        try {
            Class<?> clazz;
            TenantLine tenantLineClazz;
            TenantLine tenantLineField;
            Method[] declaredMethods;
            ResourcePatternResolver resourcePatternResolver = getResourcePatternResolver();
            MetadataReaderFactory metadata = new SimpleMetadataReaderFactory();
            for (String basePackage : basePackages) {
                if (!StrUtil.contains(basePackage, packages) || !StrUtil.startWith(basePackage, packages)) {
                    continue;
                }
                String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + '/' + DEFAULT_RESOURCE_PATTERN;
                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    MetadataReader metadataReader = metadata.getMetadataReader(resource);
                    String className = metadataReader.getClassMetadata().getClassName();
                    clazz = Class.forName(className);
                    if (clazz == null) {
                        continue;
                    }

                    tenantLineClazz = clazz.getAnnotation(TenantLine.class);
                    if (tenantLineClazz == null) {
                        continue;
                    }
                    TenantLineHelper.CACHE.put(className, tenantLineClazz.value());
                    declaredMethods = clazz.getDeclaredMethods();
                    for (Method method : declaredMethods) {
                        tenantLineField = method.getAnnotation(TenantLine.class);
                        if (tenantLineField != null) {
                            TenantLineHelper.CACHE.put(className + StrPool.DOT + method.getName(), tenantLineField.value());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("======================注意：扫描【{}】报错", basePackages, e);
        }

        return bean;
    }


    private ResourcePatternResolver getResourcePatternResolver() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }
}
