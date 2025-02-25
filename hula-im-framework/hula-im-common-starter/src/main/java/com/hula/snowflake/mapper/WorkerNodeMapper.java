/*
 * Copyright (c) 2017 Baidu, Inc. All Rights Reserve.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hula.snowflake.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.hula.snowflake.uid.worker.entity.WorkerNodeEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

/**
 * DAO for M_WORKER_NODE
 *
 * @author yutianbao
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface WorkerNodeMapper {

    /**
     * Add {@link WorkerNodeEntity}
     *
     * @param workerNodeEntity
     */
    @Insert("""
            INSERT INTO worker_node( id, host_name,port, type, launch_date,modified,created)
             VALUES (null, #{hostName},#{port},#{type},#{launchDate},#{modified}, #{created})
               """)
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void addWorkerNode(WorkerNodeEntity workerNodeEntity);

}
