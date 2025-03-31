package com.hula.xss.properties;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 忽略XSS 配置类
 *
 * @author qianqian
 * @date 2020/10/31
 */
@Data
@ConfigurationProperties(prefix = XssProperties.PREFIX)
public class XssProperties {
    public static final String PREFIX = "xss";
    private Boolean enabled = true;
    /**
     * 是否启用 RequestBody 注解标记的参数 反序列化时过滤XSS
     */
    private Boolean requestBodyEnabled = false;
    private int order = 1;
    private List<String> patterns = CollUtil.newArrayList("/*");
    private List<String> ignorePaths = CollUtil.newArrayList();
    private List<String> ignoreParamValues = CollUtil.newArrayList("noxss");
}
