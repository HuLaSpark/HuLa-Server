package com.hula.common.interceptor;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.hula.common.config.DatabaseProperties;
import com.hula.snowflake.uid.UidGenerator;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 自定义mybatis-plus 包扫描、分页、乐观锁、全表删除、ID自增
 */
@Configuration
@MapperScan("com.hula.**.mapper")
public class MyBatisPlusConfig {

	@Autowired
	private DatabaseProperties databaseProperties;

	@Autowired
	private DataSource dataSource;

	@Bean
	public MybatisPlusInterceptor interceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// 分页插件
		PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
		innerInterceptor.setOverflow(databaseProperties.getOverflow());
		// 分页单页最大500
		innerInterceptor.setMaxLimit(databaseProperties.getMaxLimit());
		interceptor.addInnerInterceptor(innerInterceptor);
		// 乐观锁插件
		interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
		if (databaseProperties.getIsBlockAttack()) {
			interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
		}
		return interceptor;
	}

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Lazy UidGenerator uidGenerator, MybatisPlusInterceptor interceptor) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();

		factoryBean.setDataSource(dataSource);
		factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*/*.xml"));
		factoryBean.setPlugins(interceptor);

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