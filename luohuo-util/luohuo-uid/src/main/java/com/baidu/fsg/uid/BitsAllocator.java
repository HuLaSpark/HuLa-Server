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
package com.baidu.fsg.uid;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.Assert;

/**
 * 对SnowFlake 的ID进行位操作的共聚类
 * <p>
 * 为UID分配64位(long)<br>
 * sign (fixed 1bit) -> deltaSecond -> workerId -> sequence(within the same second)
 *
 * @author yutianbao
 */
public class BitsAllocator {
    /**
     * Total 64 bits
     */
    public static final int TOTAL_BITS = 1 << 6;
    private final int timestampBits;
    private final int workerIdBits;
    private final int sequenceBits;
    /**
     * Max value for workId {@code &} sequence
     * 最大值
     */
    private final long maxDeltaSeconds;
    private final long maxWorkerId;
    private final long maxSequence;
    /**
     * timestamp {@code &} workerId 的移位
     */
    private final int timestampShift;
    private final int workerIdShift;
    /**
     * Bits for [sign-> second-> workId-> sequence]
     */
    private final int signBits = 1;

    /**
     * Constructor with timestampBits, workerIdBits, sequenceBits<br>
     * The highest bit used for sign, so <code>63</code> bits for timestampBits, workerIdBits, sequenceBits
     */
    public BitsAllocator(final int timestampBits, final int workerIdBits, final int sequenceBits) {
        // 确保分配了64位
        int allocateTotalBits = signBits + timestampBits + workerIdBits + sequenceBits;
        Assert.isTrue(allocateTotalBits == TOTAL_BITS, "分配的不够64位");

        // 初始化位
        this.timestampBits = timestampBits;
        this.workerIdBits = workerIdBits;
        this.sequenceBits = sequenceBits;

        // 初始化最大值
        // -1 是111111111（64个1）
        // 先将-1左移timestampBits位，得到111111100000（timestampBits个零)
        // 然后取反，得到00000....1111...（timestampBits）个1
        // 等价于2的timestampBits次方-1
        this.maxDeltaSeconds = ~(-1L << timestampBits);
        this.maxWorkerId = ~(-1L << workerIdBits);
        this.maxSequence = ~(-1L << sequenceBits);

        // 初始化移位
        this.timestampShift = workerIdBits + sequenceBits;
        this.workerIdShift = sequenceBits;
    }

    /**
     * 这里就是把不同的字段放到相应的位上
     * id的总体结构是：
     * sign (fixed 1bit) -> deltaSecond -> workerId -> sequence(within the same second)
     * deltaSecond 左移（workerIdBits + sequenceBits）位，workerId左移sequenceBits位，此时就完成了字节的分配
     * <p>
     * <p>
     * Allocate bits for UID according to delta seconds {@code &} workerId {@code &} sequence <br>
     * <b>Note that: </b>The highest bit will always be 0 for sign
     *
     * @param deltaSeconds
     * @param workerId
     * @param sequence
     * @return
     */
    public long allocate(long deltaSeconds, long workerId, long sequence) {
        return (deltaSeconds << timestampShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * Getters
     */
    public int getSignBits() {
        return signBits;
    }

    public int getTimestampBits() {
        return timestampBits;
    }

    public int getWorkerIdBits() {
        return workerIdBits;
    }

    public int getSequenceBits() {
        return sequenceBits;
    }

    public long getMaxDeltaSeconds() {
        return maxDeltaSeconds;
    }

    public long getMaxWorkerId() {
        return maxWorkerId;
    }

    public long getMaxSequence() {
        return maxSequence;
    }

    public int getTimestampShift() {
        return timestampShift;
    }

    public int getWorkerIdShift() {
        return workerIdShift;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
