package com.hula.common.interceptor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.hula.snowflake.uid.UidGenerator;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

@Configuration
public class MyBatisPlusConfig {

	@Autowired
	private DataSource dataSource;

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Lazy UidGenerator uidGenerator) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();

		factoryBean.setDataSource(dataSource);

        GlobalConfig globalConfig = new GlobalConfig();
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setIdType(IdType.ASSIGN_ID);
        globalConfig.setDbConfig(dbConfig);

		// 自定义自己的id生成器
        globalConfig.setIdentifierGenerator(new CustomSnowflakeIdGenerator(uidGenerator));
        factoryBean.setGlobalConfig(globalConfig);
        return factoryBean.getObject();
    }
}