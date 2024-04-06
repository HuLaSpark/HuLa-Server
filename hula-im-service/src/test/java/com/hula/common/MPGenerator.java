package com.hula.common;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.List;

public class MPGenerator {
    public static void main(String[] args) {
        //代码生成器
        AutoGenerator autoGenerator = new AutoGenerator();

        //数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL);//指定数据库类型
        //---------------------------数据源-----------------------------------
        assembleDev(dataSourceConfig);//配置数据源
        autoGenerator.setDataSource(dataSourceConfig);

        //全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOpen(false);
        //todo 要改输出路径
        globalConfig.setOutputDir(System.getProperty("user.dir") + "/HuLa-IM-service/src/main/java");
        //设置作者名字
        globalConfig.setAuthor("nyh");
        //去掉service的I前缀,一般只需要设置service就行
        globalConfig.setServiceImplName("%sDao");
        autoGenerator.setGlobalConfig(globalConfig);

        //包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.hula.common.user");//自定义包的路径
        packageConfig.setEntity("domain.entity");
        packageConfig.setMapper("mapper");
        packageConfig.setController("controller");
        packageConfig.setServiceImpl("dao");
        autoGenerator.setPackageInfo(packageConfig);

        //策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        //是否使用Lombok
        strategyConfig.setEntityLombokModel(true);
        //包，列的命名规则，使用驼峰规则
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
//        strategyConfig.setTablePrefix("t_");
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        //字段和表注解
        strategyConfig.setEntityTableFieldAnnotationEnable(true);
        //todo 这里修改需要自动生成的表结构
        strategyConfig.setInclude(
                "user"
        );
        //自动填充字段,在项目开发过程中,例如创建时间，修改时间,每次，都需要我们来指定，太麻烦了,设置为自动填充规则，就不需要我们赋值咯
        List<TableFill> list = new ArrayList<TableFill>();
        TableFill tableFill1 = new TableFill("create_time", FieldFill.INSERT);
        TableFill tableFill2 = new TableFill("update_time", FieldFill.INSERT_UPDATE);
        list.add(tableFill1);
        list.add(tableFill2);

//        strategyConfig.setTableFillList(list);
        autoGenerator.setStrategy(strategyConfig);

        //执行
        autoGenerator.execute();

    }
    //todo 这里修改你的数据源
    public static void assembleDev(DataSourceConfig dataSourceConfig) {
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("123456");
        dataSourceConfig.setUrl("jdbc:mysql://localhost:3306/hula-im?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC");
    }
}