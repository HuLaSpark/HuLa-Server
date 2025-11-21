package com.luohuo.basic.model.cache;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import com.luohuo.basic.utils.ArgumentAssert;

import java.time.Duration;
import java.util.ArrayList;


/**
 * cache key
 * <p>
 * key命名风格
 * 【推荐】 Redis key命名需具有可读性以及可管理性，不该使用含义不清的key以及特别长的key名；
 * 【强制】以英文字母开头，命名中只能出现小写字母、数字、英文点号(.)和英文半角冒号(:)；
 * 【强制】不要包含特殊字符，如下划线、空格、换行、单双引号以及其他转义字符；
 * <p>
 * 命名规范
 * 【强制】命名规范：[前缀:][租户编码:][服务模块名:]业务类型[:业务字段][:value类型][:业务值]
 * 0）前缀： 可选。 用来区分不同项目，不同环境。 如：区分luohuo-cloud 或 luohuo-boot、luohuo-cloud的dev或test环境
 * 1）租户ID： 可选。 用来区分不同租户数据缓存。 如： 内置租户和阿里巴巴租户分别用0000 和 1111 来区分。
 * 2）服务模块名：可选。 用来区分不同服务或功能模块的缓存。 如： 仅在权限服务使用和仅在消息服务使用的缓存分别用authority和msg来区分，多个服务共用的缓存可以不设置该段，或者设置为common。
 * 3) 业务类型： 必填。 用来区分不同业务类型的数据缓存。 通常设置为表名。 同一key有多个业务类型时， 使用英文半角点号 (.)分割，用来表示一个完整的语义。 如user.activity 表示存储用户和活动、 user表示存储用户如：用户表、应用表分别用 user、application来区分。
 * 4) 业务字段: 可选。用来区分业务值是那个字段。 通常设置为字段名。 同一key有多个业务类型时，业务字段对应多个业务类型。使用英文半角点号 (.)分割，用来表示一个完整的语义。 如业务类型为 user.activity 表示存储用户和活动， 则应该用id.id 跟user.activity 对应，表示key中包user的id 和 activity的id
 * 5) value类型： 可选。 用来区分value。 Redis key命名包含key所代表的value类型，以提高可读性。 如： obj 表示value是对象，number表示value是数字， string 表示value是字符串。
 * 6) 业务值 ：可选。 用来区分同一业务类型的不同行的数据缓存。如：用户表id为1的数据和id为2的，分别是指该值为1和2。
 * <p>
 * 示例：
 * 注意： => 左边表示缓存的完整key ， => 右边表示缓存存储的值
 * <p>
 * 0000:authority:user.activity:id.id:1:3:number => [1,2,3,4]
 * 表示：用户1活动3的奖品： 租户(0000)，权限服务(authority)中用户表(user)的用户id(id)为 1, 活动表(activity)的活动id(id)为 3 的数据，存储在list结构的缓存中的值是(number)类型的 [1, 2, 3, 4]
 * 0000:authority:user:id:1:obj => {"id:":1, "name:"张三"}
 * 表示：租户(0000)，权限服务(authority)中用户表(user)的用户id(id)为 1 的数据，存储(obj)类型的值 ({"id:":1, "name:"张三"})
 * 0000:authority:user:account:zhangsan:obj => {"id:":1, "name:"张三"}
 * 表示：租户(0000)，权限服务(authority)中用户表(user)的用户账号(account)为 zhangsan 的数据，存储(obj)类型的值 ({"id:":1, "name:"张三"})
 * tenant:id:1:obj => {"id:":1, "name:"租户1"}
 * 表示：租户表(tenant)的租户id(id)为 1 的数据，存储(obj)类型的值 (** {"id:":1, "name:"租户1"}**)
 *
 * @author 乾乾
 */
@FunctionalInterface
public interface CacheKeyBuilder {

    /**
     * 缓存前缀，用于区分项目，环境等等
     *
     * @return 缓存前缀
     */
    default String getPrefix() {
        return null;
    }

    /**
     * 租户ID，用于区分租户
     * <p>
     * 非租户模式设置成空字符串
     *
     * @return 租户ID
     */
    default String getTenant() {
        return null;
    }

    /**
     * 设置企业id
     *
     * @param tenantId 企业id
     * @return 构造器
     */
    default CacheKeyBuilder setTenantId(Long tenantId) {
        return this;
    }

    /**
     * 服务模块名，用于区分后端服务、前端模块等
     *
     * @return 服务模块名
     */
    default String getModular() {
        return null;
    }


    /**
     * key的业务类型， 用于区分表
     *
     * @return 通常是表名
     */
    @NonNull
    String getTable();

    /**
     * key的字段名， 用于区分字段
     *
     * @return 通常是key的字段名
     */
    default String getField() {
        return null;
    }

    /**
     * 缓存的value存储的类型
     *
     * @return value类型
     */
    default ValueType getValueType() {
        return ValueType.obj;
    }

    /**
     * 缓存自动过期时间
     *
     * @return 缓存自动过期时间
     */
    @Nullable
    default Duration getExpire() {
        return null;
    }

    /**
     * 获取通配符
     *
     * @return key 前缀
     */
    default String getPattern() {
        return StrUtil.format("*:{}:*", getTable());
    }

    /**
     * 构建通用KV模式 的 cache key
     * 兼容 redis caffeine
     *
     * @param uniques 参数
     * @return cache key
     */
    default CacheKey key(Object... uniques) {
        String key = getKey(uniques);
        ArgumentAssert.notEmpty(key, "key 不能为空");
        return new CacheKey(key, getExpire());
    }

    /**
     * 构建 redis 类型的 hash cache key
     *
     * @param field   field
     * @param uniques 动态参数
     * @return cache key
     */
    default CacheHashKey hashFieldKey(@NonNull Object field, Object... uniques) {
        String key = getKey(uniques);

        ArgumentAssert.notEmpty(key, "key 不能为空");
        ArgumentAssert.notNull(field, "field 不能为空");
        return new CacheHashKey(key, field, getExpire());
    }

    /**
     * 构建 redis 类型的 hash cache key （无field)
     *
     * @param uniques 动态参数
     * @return cache key
     */
    default CacheHashKey hashKey(Object... uniques) {
        String key = getKey(uniques);

        ArgumentAssert.notEmpty(key, "key 不能为空");
        return new CacheHashKey(key, null, getExpire());
    }

    /**
     * 根据动态参数 拼接key
     * <p>
     * key命名规范：[前缀:][租户ID:][服务模块名:]业务类型[:业务字段][:value类型][:业务值]
     *
     * @param uniques 动态参数
     * @return 字符串型的缓存的key
     */
    private String getKey(Object... uniques) {
        ArrayList<String> regionList = new ArrayList<>();
        String prefix = this.getPrefix();
        if (StrUtil.isNotEmpty(prefix)) {
            regionList.add(prefix);
        }

        String tenant = this.getTenant();
        // 租户编码：存储默认库的全局缓存，可以重写getTenant并返回null
        if (StrUtil.isNotEmpty(tenant)) {
            regionList.add(tenant);
        }
        // 服务模块名
        String modular = getModular();
        if (StrUtil.isNotEmpty(modular)) {
            regionList.add(modular);
        }
        // 业务类型
        String table = this.getTable();
        ArgumentAssert.notEmpty(table, "缓存业务类型不能为空");
        regionList.add(table);
        // 业务字段
        String field = getField();
        if (StrUtil.isNotEmpty(field)) {
            regionList.add(field);
        }
        // value类型
        ValueType valueType = getValueType();
        if (valueType != null) {
            regionList.add(valueType.name());
        }

        // 业务值
        for (Object unique : uniques) {
            if (ObjectUtil.isNotEmpty(unique)) {
                regionList.add(String.valueOf(unique));
            }
        }
        return CollUtil.join(regionList, StrPool.COLON);
    }

    enum ValueType {
        /**
         * 对象
         */
        obj,
        /**
         * 字符串
         */
        string,
        /**
         * 数字
         */
        number,
    }
}
