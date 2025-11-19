package com.luohuo.flex.generator.utils;

import com.luohuo.basic.database.properties.DatabaseProperties;
import com.luohuo.flex.generator.enumeration.ProjectTypeEnum;
import com.luohuo.flex.generator.vo.save.ProjectGeneratorVO;

/**
 * @author tangyh
 * @version v1.0
 * @date 2022/4/5 5:54 PM
 * @create [2022/4/5 5:54 PM ] [tangyh] [初始创建]
 */
public class ProjectUtilsTest {
    public static void main(String[] args) {
        ProjectGeneratorVO vo = new ProjectGeneratorVO();
        vo.setProjectPrefix("luohuo");
        vo.setOutputDir("/Users/luohuo/gitlab/luohuo-cloud-pro-datasource-column");
        vo.setType(ProjectTypeEnum.CLOUD);
        vo.setAuthor("乾乾");
        vo.setServiceName("test");
        vo.setModuleName("test");
        vo.setParent("com.luohuo.flex");
        vo.setGroupId("com.luohuo.flex");
        vo.setUtilParent("com.luohuo.basic");
        vo.setVersion("3.0.0");
        vo.setDescription("测试服务");
        vo.setServerPort(8080);
        ProjectUtils.generator(vo, new DatabaseProperties());
    }
}
