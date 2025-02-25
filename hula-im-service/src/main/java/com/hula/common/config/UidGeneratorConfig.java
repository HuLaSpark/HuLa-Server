package com.hula.common.config;

import com.hula.snowflake.mapper.WorkerNodeMapper;
import com.hula.snowflake.uid.UidGenerator;
import com.hula.snowflake.uid.impl.DefaultUidGenerator;
import com.hula.snowflake.uid.worker.DisposableWorkerIdAssigner;
import com.hula.snowflake.uid.worker.WorkerIdAssigner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DatabaseProperties.class)
public class UidGeneratorConfig {

    @Bean
    public UidGenerator defaultUidGenerator(
            DatabaseProperties databaseProperties,
            WorkerIdAssigner workerIdAssigner) {

        DefaultUidGenerator generator = new DefaultUidGenerator();
        generator.setTimeBits(databaseProperties.getTimeBits());
        generator.setWorkerBits(databaseProperties.getWorkerBits());
        generator.setSeqBits(databaseProperties.getSeqBits());
        generator.setEpochStr(databaseProperties.getEpochStr());
        generator.setWorkerIdAssigner(workerIdAssigner);
        return generator;
    }

	@Bean
	public DisposableWorkerIdAssigner disposableWorkerIdAssigner(WorkerNodeMapper workerNodeMapper) {
		return new DisposableWorkerIdAssigner(workerNodeMapper);
	}

}