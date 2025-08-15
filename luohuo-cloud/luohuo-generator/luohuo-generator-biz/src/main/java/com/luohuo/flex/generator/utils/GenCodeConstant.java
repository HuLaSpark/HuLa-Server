package com.luohuo.flex.generator.utils;


import com.baomidou.mybatisplus.core.toolkit.StringPool;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author zuihou
 * @date 2022/3/1 16:59
 */
public interface GenCodeConstant {

    String ENUM = "Enum";
    String SAVE_VO = "SaveVO";
    String UPDATE_VO = "UpdateVO";
    String RESULT_VO = "ResultVO";
    String PAGE_QUERY = "PageQuery";
    String ENTITY = "Entity";
    String MANAGER = "Manager";
    String MANAGER_IMPL = "ManagerImpl";
    String SERVICE = "Service";
    String SERVICE_IMPL = "ServiceImpl";
    String MAPPER = "Mapper";
    String XML = "Xml";
    String CONTROLLER = "Controller";
    String PARENT = "Parent";

    String FACADE_SERVICE_SUFFIX = "facade";
    String API_SERVICE_SUFFIX = "api";
    String BOOT_IMPL_SERVICE_SUFFIX = "boot-impl";
    String CLOUD_IMPL_SERVICE_SUFFIX = "cloud-impl";
    String ENTITY_SERVICE_SUFFIX = "entity";
    String BIZ_SERVICE_SUFFIX = "biz";
    String CONTROLLER_SERVICE_SUFFIX = "controller";
    String SERVER_SERVICE_SUFFIX = "server";

    String JAVA_TMPDIR = "java.io.tmpdir";
    String UTF8 = StandardCharsets.UTF_8.name();
    String UNDERLINE = "_";

    String JAVA_SUFFIX = StringPool.DOT_JAVA;
    String XML_SUFFIX = ".xml";
    String FTL_SUFFIX = ".ftl";
    String SQL_SUFFIX = ".sql";

    String TEMPLATE_ENUM = "/templates/backend/java/enum.java.ftl";
    String TEMPLATE_SAVE_VO = "/templates/backend/java/saveVO.java.ftl";
    String TEMPLATE_UPDATE_VO = "/templates/backend/java/updateVO.java.ftl";
    String TEMPLATE_RESULT_VO = "/templates/backend/java/resultVO.java.ftl";
    String TEMPLATE_PAGE_QUERY = "/templates/backend/java/pageQuery.java.ftl";
    String TEMPLATE_SQL = "/templates/backend/sql/menu.sql.ftl";
    /**
     * 实体模板路径
     */
    String TEMPLATE_ENTITY_JAVA = "/templates/backend/java/entity.java.ftl";

    /**
     * MapperXml模板路径
     */
    String TEMPLATE_XML = "/templates/backend/xml/mapper.xml.ftl";
    /**
     * Mapper模板路径
     */
    String TEMPLATE_MAPPER = "/templates/backend/java/mapper.java.ftl";
    String TEMPLATE_MANAGER = "/templates/backend/java/manager.java.ftl";
    String TEMPLATE_MANAGER_IMPL = "/templates/backend/java/managerImpl.java.ftl";
    /**
     * Service模板路径
     */
    String TEMPLATE_SERVICE = "/templates/backend/java/service.java.ftl";

    /**
     * ServiceImpl模板路径
     */
    String TEMPLATE_SERVICE_IMPL = "/templates/backend/java/serviceImpl.java.ftl";

    /**
     * 控制器模板路径
     */
    String TEMPLATE_CONTROLLER = "/templates/backend/java/controller.java.ftl";

    /** API请求页 */
    String TEMPLATE_WEB_PRO_SIMPLE_API = "/templates/web/vben/simple/typescript/api.ts.ftl";
    /** API泛型Model页 */
    String TEMPLATE_WEB_PRO_SIMPLE_MODEL = "/templates/web/vben/simple/typescript/model.ts.ftl";
    /** 多语言英文 */
    String TEMPLATE_WEB_PRO_SIMPLE_LANG_EN = "/templates/web/vben/simple/typescript/langEn.ts.ftl";
    /** 多语言中文 */
    String TEMPLATE_WEB_PRO_SIMPLE_LANG_ZH = "/templates/web/vben/simple/typescript/langZh.ts.ftl";
    /** 单表 tsx */
    String TEMPLATE_WEB_PRO_SIMPLE_DATA = "/templates/web/vben/simple/typescript/data.tsx.ftl";
    /** 单表 列表页 */
    String TEMPLATE_WEB_PRO_SIMPLE_INDEX = "/templates/web/vben/simple/vue/index.vue.ftl";
    /** 单表 弹窗编辑页 */
    String TEMPLATE_WEB_PRO_SIMPLE_EDIT = "/templates/web/vben/simple/vue/edit.vue.ftl";
    /** 单表 跳转编辑页 */
    String TEMPLATE_WEB_PRO_SIMPLE_JUMP_EDIT = "/templates/web/vben/simple/vue/jumpEdit.vue.ftl";

    /** 主从页面 列表页 */
    String TEMPLATE_WEB_PRO_MAIN_INDEX = "/templates/web/vben/mainSub/vue/index.vue.ftl";
    /** 主从页面 弹窗编辑页 */
    String TEMPLATE_WEB_PRO_MAIN_EDIT = "/templates/web/vben/mainSub/vue/edit.vue.ftl";
    /** 主从页面 跳转编辑页 */
    String TEMPLATE_WEB_PRO_MAIN_JUMP_EDIT = "/templates/web/vben/mainSub/vue/jumpEdit.vue.ftl";
    /** 主从页面 从表列表页 */
    String TEMPLATE_WEB_PRO_MAIN_SUB_INDEX = "/templates/web/vben/mainSub/vue/subIndex.vue.ftl";
    /** 主从页面 从表列表页tsx */
    String TEMPLATE_WEB_PRO_MAIN_SUB_DATA = "/templates/web/vben/mainSub/typescript/subData.tsx.ftl";

    /** 树结构主页 */
    String TEMPLATE_WEB_PRO_TREE_INDEX = "/templates/web/vben/tree/vue/index.vue.ftl";
    /** 树结构树结构页 */
    String TEMPLATE_WEB_PRO_TREE_TREE = "/templates/web/vben/tree/vue/tree.vue.ftl";
    /** 树结构编辑页 */
    String TEMPLATE_WEB_PRO_TREE_EDIT = "/templates/web/vben/tree/vue/edit.vue.ftl";


    /** API请求页 */
    String TEMPLATE_WEB_SOYBEAN_SIMPLE_API = "/templates/web/soybean/simple/typescript/api.ts.ftl";
    /** API泛型Model页 */
    String TEMPLATE_WEB_SOYBEAN_SIMPLE_MODEL = "/templates/web/soybean/simple/typescript/model.ts.ftl";
    /** 多语言英文 */
    String TEMPLATE_WEB_SOYBEAN_SIMPLE_LANG_EN = "/templates/web/soybean/simple/typescript/langEn.ts.ftl";
    /** 多语言中文 */
    String TEMPLATE_WEB_SOYBEAN_SIMPLE_LANG_ZH = "/templates/web/soybean/simple/typescript/langZh.ts.ftl";
    /** 单表 tsx */
    String TEMPLATE_WEB_SOYBEAN_SIMPLE_CRUD = "/templates/web/soybean/simple/typescript/crud.tsx.ftl";
    String TEMPLATE_WEB_SOYBEAN_SIMPLE_INDEX = "/templates/web/soybean/simple/vue/index.vue.ftl";
    String TEMPLATE_WEB_SOYBEAN_SIMPLE_JUMP_EDIT = "/templates/web/soybean/simple/vue/edit.vue.ftl";
    String TEMPLATE_WEB_SOYBEAN_TREE_INDEX = "/templates/web/soybean/tree/vue/index.vue.ftl";
    String TEMPLATE_WEB_SOYBEAN_TREE_TREE = "/templates/web/soybean/tree/vue/tree.vue.ftl";
    String TEMPLATE_WEB_SOYBEAN_TREE_EDIT = "/templates/web/soybean/tree/vue/edit.vue.ftl";
    String TEMPLATE_WEB_SOYBEAN_TREE_CRUD = "/templates/web/soybean/tree/typescript/crud.tsx.ftl";

    String TEMPLATE_WEB_SOYBEAN_MAIN_INDEX = "/templates/web/soybean/mainSub/vue/index.vue.ftl";


    /** API请求页 */
    String TEMPLATE_WEB_VBEN5_SIMPLE_API = "/templates/web/vben5/simple/typescript/api.ts.ftl";
    /** API泛型Model页 */
    String TEMPLATE_WEB_VBEN5_SIMPLE_MODEL = "/templates/web/vben5/simple/typescript/model.ts.ftl";
    /** 多语言英文 */
    String TEMPLATE_WEB_VBEN5_SIMPLE_LANG_EN = "/templates/web/vben5/simple/typescript/langEn.ts.ftl";
    /** 多语言中文 */
    String TEMPLATE_WEB_VBEN5_SIMPLE_LANG_ZH = "/templates/web/vben5/simple/typescript/langZh.ts.ftl";
    /** 单表 tsx */
    String TEMPLATE_WEB_VBEN5_SIMPLE_CRUD = "/templates/web/vben5/simple/typescript/crud.tsx.ftl";
    String TEMPLATE_WEB_VBEN5_SIMPLE_INDEX = "/templates/web/vben5/simple/vue/index.vue.ftl";
    String TEMPLATE_WEB_VBEN5_SIMPLE_JUMP_EDIT = "/templates/web/vben5/simple/vue/edit.vue.ftl";
    String TEMPLATE_WEB_VBEN5_TREE_INDEX = "/templates/web/vben5/tree/vue/index.vue.ftl";
    String TEMPLATE_WEB_VBEN5_TREE_TREE = "/templates/web/vben5/tree/vue/tree.vue.ftl";
    String TEMPLATE_WEB_VBEN5_TREE_EDIT = "/templates/web/vben5/tree/vue/edit.vue.ftl";
    String TEMPLATE_WEB_VBEN5_TREE_CRUD = "/templates/web/vben5/tree/typescript/crud.tsx.ftl";

    String TEMPLATE_WEB_VBEN5_MAIN_INDEX = "/templates/web/vben5/mainSub/vue/index.vue.ftl";


    String POM_FORMAT = "/templates/project/xml/{}.pom.ftl";
    String JAVA_FORMAT = "/templates/project/java/{}.ftl";
    String RESOURCE_YML_FORMAT = "/templates/project/yml/{}.ftl";
    String RESOURCE_XML_FORMAT = "/templates/project/xml/{}.ftl";
    String ROOT = "root";
    String POM_NAME = "pom.xml";

    String RUN_APPLICATION_SUFFIX = "ServerApplication.java";
    String WEB_CONFIGURATION_SUFFIX = "WebConfiguration.java";
    String EXCEPTION_CONFIGURATION_SUFFIX = "ExceptionConfiguration.java";
    String MYBATIS_CONFIGURATION_SUFFIX = "MybatisConfiguration.java";

    String BOOTSTRAP_SUFFIX = "bootstrap.yml";
    String BOOTSTRAP_DEV_SUFFIX = "bootstrap-dev.yml";
    String APPLICATION_SUFFIX = "application.yml";
    String LOGBACK_SPRING_SUFFIX = "logback-spring.xml";
    String LOGBACK_SPRING_DEV_SUFFIX = "logback-spring-dev.xml";
    String GATEWAY_SERVER_SUFFIX = "gateway-server.yml";


    String SRC_MAIN_JAVA = "src" + File.separator + "main" + File.separator + "java";
    String SRC_MAIN_RESOURCE = "src" + File.separator + "main" + File.separator + "resources";
    String SRC_TEST_JAVA = "src" + File.separator + "test" + File.separator + "java";
    String SRC_TEST_RESOURCE = "src" + File.separator + "test" + File.separator + "resources";

    String PUBLIC_MODEL_PATH = "{}-public/{}-model/" + GenCodeConstant.SRC_MAIN_JAVA;

    String KEY_SAVE_VO_PACKAGE = "saveVoPackage";
    String KEY_UPDATE_VO_PACKAGE = "updateVoPackage";
    String KEY_MANAGER_PACKAGE = "managerPackage";
    String KEY_ENTITY_PACKAGE = "entityPackage";

    String ET_LIST = "enumList";
    String ECHO_LIST = "echoList";
    String ECHO_API_LIST = "echoApiList";
    String ECHO_REF_LIST = "echoRefList";
    String ECHO_DICT_TYPE_MAP = "echoDictTypeMap";

    String ECHO_ENUM_API = "Echo.ENUM_API";
}
