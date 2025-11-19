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
package com.baidu.fsg.uid.worker;

import cn.hutool.core.util.RandomUtil;
import com.baidu.fsg.uid.utils.DockerUtils;
import com.baidu.fsg.uid.utils.NetUtils;
import com.baidu.fsg.uid.worker.entity.WorkerNodeEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.uid.dao.WorkerNodeDao;

import java.util.Date;

/**
 * Represents an implementation of {@link WorkerIdAssigner},
 * the worker id will be discarded after assigned to the UidGenerator
 * <p>
 * 基于DB自增的worker id 分配器的实现
 * <p>
 * SnowFlake中，deltaSeconds依赖时间戳，可以通过系统获取；sequence可以通过自增来控制；这俩字段都是项目可以自给自足的，而WorkerId则必须还有一个策略来提供。
 * 这个策略要保证每次服务启动的时候拿到的WorkerId都能不重复，不然就有可能集群不同的机器拿到不同的workerid，会发重复的号了；
 * 而服务启动又是个相对低频的行为，也不影响发号性能，所以可以用DB自增ID来实现。
 * DatabaseWorkerIdAssigner就是依赖DB自增ID实现的workerId分配器。
 *
 * @author yutianbao
 */
@RequiredArgsConstructor
public class DisposableWorkerIdAssigner implements WorkerIdAssigner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisposableWorkerIdAssigner.class);

    private final WorkerNodeDao workerNodeDao;

    /**
     * Assign worker id base on database.<p>
     * If there is host name {@code &} port in the environment, we considered that the node runs in Docker container<br>
     * Otherwise, the node runs on an actual machine.
     *
     * @return assigned worker id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long assignWorkerId() {
        // build worker node entity
        WorkerNodeEntity workerNodeEntity = buildWorkerNode();

        // add worker node for new (ignore the same IP + PORT)
        workerNodeDao.addWorkerNode(workerNodeEntity);
        LOGGER.info("Add worker node:" + workerNodeEntity);

        return workerNodeEntity.getId();
    }

    /**
     * Build worker node entity by IP and PORT
     */
    private WorkerNodeEntity buildWorkerNode() {
        WorkerNodeEntity workerNodeEntity = new WorkerNodeEntity();
        if (DockerUtils.isDocker()) {
            workerNodeEntity.setType(WorkerNodeType.CONTAINER.value());
            workerNodeEntity.setHostName(DockerUtils.getDockerHost());
            workerNodeEntity.setPort(DockerUtils.getDockerPort());
        } else {
            workerNodeEntity.setType(WorkerNodeType.ACTUAL.value());
            workerNodeEntity.setHostName(NetUtils.getLocalAddress());
            workerNodeEntity.setPort(System.currentTimeMillis() + "-" + RandomUtil.randomInt(100000));
        }
        workerNodeEntity.setCreated(new Date());
        workerNodeEntity.setModified(new Date());
        return workerNodeEntity;
    }

}
