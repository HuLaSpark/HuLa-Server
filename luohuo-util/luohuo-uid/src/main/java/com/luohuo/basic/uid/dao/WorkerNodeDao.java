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
package com.luohuo.basic.uid.dao;

import com.baidu.fsg.uid.worker.entity.WorkerNodeEntity;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Repository;

/**
 * DAO for M_WORKER_NODE
 *
 * @author yutianbao
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface WorkerNodeDao {

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
    // oracle 用下面2个注解！ mysql 和 sql server 用上面2个注解！
    @Insert(databaseId = "oracle", value = """
            INSERT INTO worker_node(id, host_name,port, type, launch_date,modified,created)
            VALUES (#{id}, #{hostName},#{port},#{type},#{launchDate},#{modified}, #{created})
            """)
    @SelectKey(databaseId = "oracle", statement = "SELECT WORKER_NODE_SEQ.NEXTVAL as id FROM DUAL", keyColumn = "id",
            keyProperty = "id", resultType = long.class, before = true)
    void addWorkerNode(WorkerNodeEntity workerNodeEntity);

}
