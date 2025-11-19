package com.luohuo.basic.base.request;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.utils.StrPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页参数
 *
 * @author 乾乾
 * @date 2020年02月14日16:19:36
 */
@Data
@NoArgsConstructor
@Schema(description = "分页参数")
public class PageParams<T> {

    @NotNull(message = "查询对象model不能为空")
    @Schema(description = "查询参数", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    private T model;

    @Schema(description = "每页显示数据", example = "10")
    private long size = 10;

    @Schema(description = "当前页", example = "1")
    private long current = 1;

    @Schema(description = "排序,默认id", allowableValues = "id,createTime,updateTime", example = "id")
    private String sort = SuperEntity.ID_FIELD;

    @Schema(description = "排序规则, 默认descending", allowableValues = "descending,ascending", example = "descending")
    private String order = "descending";

    @Schema(description = "扩展参数")
    private Map<String, Object> extra = MapUtil.newHashMap();


    public PageParams(long current, long size) {
        this.size = size;
        this.current = current;
    }

    /**
     * 构建分页对象
     *
     * @return 分页对象
     */
    @JsonIgnore
    public <E> IPage<E> buildPage() {
        PageParams params = this;
        return new Page(params.getCurrent(), params.getSize());
    }

    /**
     * 构建分页对象
     * <p>
     * 支持多个字段排序，用法：
     * eg.1, 参数：{order:"name,id", order:"descending,ascending" }。 排序： name desc, id asc
     * eg.2, 参数：{order:"name", order:"descending,ascending" }。 排序： name desc
     * eg.3, 参数：{order:"name,id", order:"descending" }。 排序： name desc
     *
     * @param entityClazz 字段中标注了@TableName 或 @TableId 注解的实体类。
     * @return 分页对象
     * @since 3.5.0
     */
    @JsonIgnore
    public <E> IPage<E> buildPage(Class<?> entityClazz) {
        PageParams params = this;
        //没有排序参数
        if (StrUtil.isEmpty(params.getSort())) {
            return new Page(params.getCurrent(), params.getSize());
        }

        Page<E> page = new Page(params.getCurrent(), params.getSize());

        List<OrderItem> orders = new ArrayList<>();
        String[] sortArr = StrUtil.splitToArray(params.getSort(), StrPool.COMMA);
        String[] orderArr = StrUtil.splitToArray(params.getOrder(), StrPool.COMMA);

        int len = Math.min(sortArr.length, orderArr.length);
        for (int i = 0; i < len; i++) {
            String humpSort = sortArr[i];
            // 简单的 驼峰 转 下划线
            String underlineSort = Wraps.getDbField(humpSort, entityClazz);
            orders.add(StrUtil.equalsAny(orderArr[i], "ascending", "ascend", "asc") ? OrderItem.asc(underlineSort) : OrderItem.desc(underlineSort));
        }

        page.setOrders(orders);

        return page;
    }

    /**
     * 计算当前分页偏移量
     */
    @JsonIgnore
    public long offset() {
        long current = this.current;
        if (current <= 1L) {
            return 0L;
        }
        return (current - 1) * this.size;
    }

    @JsonIgnore
    public PageParams<T> put(String key, Object value) {
        if (this.extra == null) {
            this.extra = new HashMap<>(16);
        }
        this.extra.put(key, value);
        return this;
    }

    @JsonIgnore
    public PageParams<T> putAll(Map<String, Object> extra) {
        if (this.extra == null) {
            this.extra = new HashMap<>(16);
        }
        this.extra.putAll(extra);
        return this;
    }
}
