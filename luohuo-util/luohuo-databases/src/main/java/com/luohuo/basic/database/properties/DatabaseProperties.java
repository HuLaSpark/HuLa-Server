package com.luohuo.basic.database.properties;

import com.baidu.fsg.uid.buffer.RejectedPutBufferHandler;
import com.baidu.fsg.uid.buffer.RejectedTakeBufferHandler;
import com.baidu.fsg.uid.buffer.RingBuffer;
import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import com.luohuo.basic.constant.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.luohuo.basic.database.properties.DatabaseProperties.PREFIX;


/**
 * 客户端认证配置
 *
 * @author 乾乾
 * @date 2018/11/20
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = PREFIX)
public class DatabaseProperties {
    public static final String PREFIX = Constants.PROJECT_PREFIX + ".database";
    /**
     * 溢出总页数后是否进行处理
     */
    protected Boolean overflow = true;
    /**
     * 生成 countSql 优化掉 join
     * 现在只支持 left join
     */
    protected Boolean optimizeJoin = true;
    /**
     * 是否启用 防止全表更新与删除插件
     */
    private Boolean isBlockAttack = false;
    /**
     * 是否启用  sql性能规范插件
     */
    private Boolean isIllegalSql = false;
    /**
     * 是否启用 seata
     * 仅仅 DATASOURCE 模式有效
     */
    private Boolean isSeata = false;
    /**
     * 是否p6spy在控制台打印日志
     * 仅仅 DATASOURCE 模式有效
     */
    private Boolean p6spy = false;
    /**
     * 单页分页条数限制
     */
    private long maxLimit = -1;
    private DbType dbType;

    /**
     * 是否启用数据权限
     */
    private Boolean isDataScope = true;

    /**
     * SCHEMA模式 租户库 前缀
     */
    private String tenantDatabasePrefix = "luohuo_base";
    /**
     * DATASOURCE模式 租户库 前缀
     * <p>
     * 新增租户时，需要动态创建的数据库前缀。
     * <p>
     * 你的所有后台服务启动时，链接那些租户数据库，这里就要配置几个前缀
     * <p>
     * 如：luohuo项目原始的服务（基础服务、租户服务、认证服务、消息服务、文件服务、网关服务）
     * 总共链接了2个租户库： luohuo_base_{TenantId}、luohuo_extend_{TenantId}， 这里就要配置2个前缀
     */
    private List<String> initDatabasePrefix = Collections.emptyList();

    /**
     * 多租户模式
     */
    private MultiTenantType multiTenantType;
    /**
     * 租户id 列名
     * <p>
     * 使用于 COLUMN 模式
     */
    private String tenantIdColumn = "tenant_id";
    /**
     * 在执行sql时，租户插件 不会自动拼接租户ID的 表名
     * 仅 COLUMN 和 DATASOURCE_COLUMN 模式有效
     */
    private List<String> ignoreTable = new ArrayList<>();
    /**
     * 在执行sql时，租户插件 不会自动拼接租户ID的 表前缀
     * 仅 COLUMN 和 DATASOURCE_COLUMN 模式有效
     */
    private List<String> ignoreTablePrefix = new ArrayList<>();
    /**
     * id 类型
     */
    private IdType idType = IdType.DEFAULT;
    /**
     * 百度 uid 生成策略
     */
    private CacheId cacheId = new CacheId();
    private DefaultId defaultId = new DefaultId();
    private HutoolId hutoolId = new HutoolId();

    @Data
    public static class HutoolId {
        /**
         * 终端ID (0-31)      单机配置0 即可。 集群部署，根据情况每个实例自增即可。
         */
        private Long workerId = 0L;
        /**
         * 数据中心ID (0-31)   单机配置0 即可。 集群部署，根据情况每个实例自增即可。
         */
        private Long dataCenterId = 0L;
    }

    /**
     * 参数说明参考：https://github.com/baidu/uid-generator
     * 长期运行：重启频率为12次/天, 那么配置成{"workerBits":23,"timeBits":31,"seqBits":9}时, 可支持28个节点以整体并发量14400 UID/s的速度持续运行68年
     * 长期运行+集群数量少+并发稍高：{"workerBits":20,"timeBits":31,"seqBits":12}这样的配置可以68年，100W次重启，单机每秒4096个并发的情况
     * 频繁重启：重启频率为24*12次/天, 那么配置成{"workerBits":27,"timeBits":30,"seqBits":6}时, 可支持37个节点以整体并发量2400 UID/s的速度持续运行34年.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CacheId extends DefaultId {

        /**
         * RingBuffer size扩容参数, 可提高UID生成的吞吐量.
         * 默认:3， 原bufferSize=1024, 扩容后bufferSize= 1024 << 3 = 8192
         */
        private int boostPower = 3;
        /**
         * 指定何时向RingBuffer中填充UID, 取值为百分比(0, 100), 默认为50
         * 举例: bufferSize=1024, paddingFactor=50 -> threshold=1024 * 50 / 100 = 512.
         * 当环上可用UID数量 < 512时, 将自动对RingBuffer进行填充补全
         */
        private int paddingFactor = RingBuffer.DEFAULT_PADDING_PERCENT;
        /**
         * 另外一种RingBuffer填充时机, 在Schedule线程中, 周期性检查填充
         * 默认:不配置此项, 即不实用Schedule线程. 如需使用, 请指定Schedule线程时间间隔, 单位:秒
         */
        private Long scheduleInterval = 5 * 60L;
        /**
         * 拒绝策略: 当环已满, 无法继续填充时
         * 默认无需指定, 将丢弃Put操作, 仅日志记录. 如有特殊需求, 请实现RejectedPutBufferHandler接口(支持Lambda表达式)
         */
        private Class<? extends RejectedPutBufferHandler> rejectedPutBufferHandlerClass;
        /**
         * 拒绝策略: 当环已空, 无法继续获取时
         * 默认无需指定, 将记录日志, 并抛出UidGenerateException异常. 如有特殊需求, 请实现RejectedTakeBufferHandler接口(支持Lambda表达式)
         */
        private Class<? extends RejectedTakeBufferHandler> rejectedTakeBufferHandlerClass;
    }

    @Data
    public static class DefaultId {

        /**
         * 当前时间，相对于时间基点"${epochStr}"的增量值，单位：秒.
         * <p>
         * 28: 大概可以使用 8.7年, 28位即最大表示2^28的数值的秒数
         * 30: 大概可以使用 34年, 30位即最大表示2^30的数值的秒数
         * 31: 大概可以使用 68年, 31位即最大表示2^31的数值的秒数
         */
        private int timeBits = 41;
        /**
         * 机器id。内置实现为在启动时由数据库分配，默认分配策略为用后即弃，后续可提供复用策略。
         * <p>
         * 20：100W次重启
         * 22: 最多可支持约420w次机器启动。内置实现为在启动时由数据库分配。420w = 2^22
         * 23：800w次重启  12次/天
         * 27: 1.3亿次重启 24*12次/天
         */
        private int workerBits = 13;
        /**
         * 每秒下的并发序列，13 bits可支持每秒8192个并发，即2^13个并发
         * 9: 512 并发
         * 13: 8192 并发
         */
        private int seqBits = 9;
        /**
         * 客户历元，单位为秒。For example 2016-05-20 (ms: 1463673600000)
         * 可以改成你的项目开始开始的时间
         */
        private String epochStr = "2025-02-25";
        /**
         * 当在低频模式下时，序号始终为0，导致生成ID始终为偶数<br>
         * 此属性用于限定一个随机上限，在不同毫秒下生成序号时，给定一个随机数，避免偶数问题。<br>
         * 注意次数必须小于bitsAllocator.getMaxSequence()，{@code 0}表示不使用随机数。<br>
         * 这个上限不包括值本身。
         */
        private Long randomSequenceLimit = 0L;
    }

}
