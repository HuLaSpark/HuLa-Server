package com.luohuo.basic.swagger2.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import com.luohuo.basic.constant.Constants;

import java.util.List;

import static com.luohuo.basic.swagger2.properties.SwaggerProperties.PREFIX;

/**
 * swagger2 属性配置
 * 必须配置 prefix ，才能有提示
 *
 * @author 乾乾
 * @date 2018/11/18 9:17
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class SwaggerProperties {
    public static final String PREFIX = Constants.PROJECT_PREFIX + ".swagger";

    /**
     * 标题
     **/
    private String title = "在线文档";
    /**
     * 描述
     **/
    private String description = "luohuo-cloud 在线文档";
    /**
     * 版本
     **/
    private String version = "1.0";
    /**
     * 许可证
     **/
    private String license = "";
    /**
     * 许可证URL
     **/
    private String licenseUrl = "";
    /**
     * 服务条款URL
     **/
    private String termsOfServiceUrl = "";

    private Contact contact = new Contact();

    /**
     * 全局参数配置
     **/
    private List<GlobalOperationParameter> globalOperationParameters;

    @Setter
    @Getter
    public static class GlobalOperationParameter {
        /**
         * 参数名
         **/
        private String name;

        /**
         * 描述信息
         **/
        private String description = "全局参数";

        /**
         * 指定参数类型
         **/
        private String modelRef = "String";

        /**
         * 参数放在哪个地方:header,query,path,body.form
         **/
        private String parameterType = "header";

        /**
         * 参数是否必须传
         **/
        private Boolean required = false;
        /**
         * 默认值
         */
        private String defaultValue = "";
        /**
         * 允许为空
         */
        private Boolean allowEmptyValue = true;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Contact {
        /**
         * 联系人
         **/
        private String name = "";
        /**
         * 联系人url
         **/
        private String url = "";
        /**
         * 联系人email
         **/
        private String email = "";
    }

}
