package com.hula.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.io.IOException;


/**
 * @author nyh
 */
@Configuration
public class SpringDocConfig {

    private Info info() throws IOException, XmlPullParserException {
        // 读取pom.xml内容，构建模型
        Model model = new MavenXpp3Reader().read(new FileReader("pom.xml"));
        // 服务版本号
        String revision = model.getProperties().getProperty("revision");
        return new Info()
                .title("hula")
                .description("hula接口文档")
                .contact(new Contact().name("nongyehong").email("2439646234@qq.com").url("https://github.com/nongyehong/HuLa-IM-Tauri"))
                .version(revision);
    }

    @Bean
    public OpenAPI springShopOpenAPI() throws XmlPullParserException, IOException {
        return new OpenAPI()
                .info(info());
    }
}