package com.luohuo.basic.database.mybatis.conditions;


import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.luohuo.basic.database.mybatis.conditions.query.LbQueryWrap;
import com.luohuo.basic.database.mybatis.conditions.query.QueryWrap;
import com.luohuo.basic.database.mybatis.conditions.update.LbUpdateWrap;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.basic.utils.DateUtils;
import com.luohuo.basic.utils.StrHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import static com.luohuo.basic.utils.StrPool.PERCENT;
import static com.luohuo.basic.utils.StrPool.UNDERSCORE;

/**
 * Wrappers 工具类， 该方法的主要目的是为了 缩短代码长度
 *
 * @author 乾乾
 * @date 2019/06/14
 */
public final class Wraps {
    /**
     * 开始时间
     */
    public static final String ST = "_st";
    /**
     * 结束时间
     */
    public static final String ED = "_ed";
    /**
     * 开始时间
     */
    public static final String START = "_start";
    /**
     * 结束时间
     */
    public static final String END = "_end";
    /**
     * 等于
     */
    public static final String EQ = "_eq";
    /**
     * 不等于
     */
    public static final String NE = "_ne";
    /**
     * 大于等于
     */
    public static final String GE = "_ge";
    /**
     * 大于
     */
    public static final String GT = "_gt";
    /**
     * 小于
     */
    public static final String LT = "_lt";
    /**
     * 小于等于
     */
    public static final String LE = "_le";
    /**
     * 模糊
     */
    public static final String LIKE = "_like";
    /**
     * 左模糊
     */
    public static final String LIKE_LEFT = "_likeLeft";
    /**
     * 右模糊
     */
    public static final String LIKE_RIGHT = "_likeRight";
    /**
     * 范围内
     */
    public static final String IN = "_in";

    private Wraps() {
        // ignore
    }

    /**
     * 获取 QueryWrap&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return QueryWrapper&lt;T&gt;
     */
    public static <T> QueryWrap<T> q() {
        return new QueryWrap<>();
    }

    /**
     * 获取 QueryWrap&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return QueryWrapper&lt;T&gt;
     */
    public static <T> QueryWrap<T> q(T entity) {
        return new QueryWrap<>(entity);
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return LambdaQueryWrapper&lt;T&gt;
     */
    public static <T> LbQueryWrap<T> lbQ() {
        return new LbQueryWrap<>();
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaQueryWrapper&lt;T&gt;
     */
    public static <T> LbQueryWrap<T> lbQ(T entity) {
        return new LbQueryWrap<>(entity);
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @return LambdaUpdateWrapper&lt;T&gt;
     */
    public static <T> LbUpdateWrap<T> lbU() {
        return new LbUpdateWrap<>();
    }

    /**
     * 获取 HyLambdaQueryWrapper&lt;T&gt;
     *
     * @param entity 实体类
     * @param <T>    实体类泛型
     * @return LambdaUpdateWrapper&lt;T&gt;
     */
    public static <T> LbUpdateWrap<T> lbU(T entity) {
        return new LbUpdateWrap<>(entity);
    }


    public static <Entity> LbQueryWrap<Entity> lbq(Entity model, Map<String, Object> extra, Class<Entity> modelClazz) {
        return Wraps.q(model, extra, modelClazz).lambda();
    }

    /**
     * 构建查询条件
     * 1. 若model不为空，则将model中不为空的参数拼接到sql中；
     * 2. 若extra中有 _st、_ed、_ge、_gt、_le、_lt、_eq、_ne、_like、_likeLeft、_likeRigth 等结尾的参数，在sql中拼接为相应的查询条件
     *
     * @param model      model 条件对象实例
     * @param extra      extra 扩展参数
     * @param modelClazz modelClazz 条件对象类型
     * @return com.luohuo.basic.database.mybatis.conditions.query.QueryWrap<Entity>
     * @author 乾乾
     * @date 2021/8/26 8:47 下午
     * @create [2021/8/26 8:47 下午 ] [tangyh] [初始创建]
     */
    public static <Entity> QueryWrap<Entity> q(Entity model, Map<String, Object> extra, Class<Entity> modelClazz) {
        QueryWrap<Entity> wrapper = model != null ? Wraps.q(model) : Wraps.q();

        if (MapUtil.isNotEmpty(extra)) {
            //拼装区间
            for (Map.Entry<String, Object> field : extra.entrySet()) {
                String key = field.getKey();
                Object value = field.getValue();
                if (ObjectUtil.isEmpty(value)) {
                    continue;
                }
                if (key.endsWith(Wraps.ST)) {
                    String beanField = StrUtil.subBefore(key, Wraps.ST, true);
                    wrapper.ge(Wraps.getDbField(beanField, modelClazz), DateUtils.getStartTime(value.toString()));
                } else if (key.endsWith(Wraps.ED)) {
                    String beanField = StrUtil.subBefore(key, Wraps.ED, true);
                    wrapper.le(Wraps.getDbField(beanField, modelClazz), DateUtils.getEndTime(value.toString()));
                } else if (key.endsWith(Wraps.START)) {
                    String beanField = StrUtil.subBefore(key, Wraps.START, true);
                    wrapper.ge(Wraps.getDbField(beanField, modelClazz), DateUtils.parseAsLocalDateTime(value.toString()));
                } else if (key.endsWith(Wraps.END)) {
                    String beanField = StrUtil.subBefore(key, Wraps.END, true);
                    wrapper.le(Wraps.getDbField(beanField, modelClazz), DateUtils.parseAsLocalDateTime(value.toString()));
                } else if (key.endsWith(Wraps.GE)) {
                    String beanField = StrUtil.subBefore(key, Wraps.GE, true);
                    wrapper.ge(Wraps.getDbField(beanField, modelClazz), value);
                } else if (key.endsWith(Wraps.GT)) {
                    String beanField = StrUtil.subBefore(key, Wraps.GT, true);
                    wrapper.gt(Wraps.getDbField(beanField, modelClazz), value);
                } else if (key.endsWith(Wraps.LT)) {
                    String beanField = StrUtil.subBefore(key, Wraps.LT, true);
                    wrapper.lt(Wraps.getDbField(beanField, modelClazz), value);
                } else if (key.endsWith(Wraps.LE)) {
                    String beanField = StrUtil.subBefore(key, Wraps.LE, true);
                    wrapper.le(Wraps.getDbField(beanField, modelClazz), value);
                } else if (key.endsWith(Wraps.NE)) {
                    String beanField = StrUtil.subBefore(key, Wraps.NE, true);
                    wrapper.ne(Wraps.getDbField(beanField, modelClazz), value);
                } else if (key.endsWith(Wraps.EQ)) {
                    String beanField = StrUtil.subBefore(key, Wraps.EQ, true);
                    wrapper.eq(Wraps.getDbField(beanField, modelClazz), value);
                } else if (key.endsWith(Wraps.LIKE)) {
                    String beanField = StrUtil.subBefore(key, Wraps.LIKE, true);
                    wrapper.like(Wraps.getDbField(beanField, modelClazz), value);
                } else if (key.endsWith(Wraps.LIKE_LEFT)) {
                    String beanField = StrUtil.subBefore(key, Wraps.LIKE_LEFT, true);
                    wrapper.likeLeft(Wraps.getDbField(beanField, modelClazz), value);
                } else if (key.endsWith(Wraps.LIKE_RIGHT)) {
                    String beanField = StrUtil.subBefore(key, Wraps.LIKE_RIGHT, true);
                    wrapper.likeRight(Wraps.getDbField(beanField, modelClazz), value);
                } else if (key.endsWith(Wraps.IN)) {
                    String beanField = StrUtil.subBefore(key, Wraps.IN, true);
                    wrapper.in(Wraps.getDbField(beanField, modelClazz), Wraps.castList(value));
                }
            }
        }
        return wrapper;
    }

    /**
     * 根据 bean字段 反射出 数据库字段
     *
     * @param beanField 字段
     * @param clazz     类型
     * @return 数据库字段名
     */
    public static String getDbField(String beanField, Class<?> clazz) {
        ArgumentAssert.notNull(clazz, "实体类不能为空");
        ArgumentAssert.notEmpty(beanField, "字段名不能为空");
        Field field = ReflectUtil.getField(clazz, beanField);
        ArgumentAssert.notNull(field, "在类：{}中找不到属性：{}", clazz.getSimpleName(), beanField);

        TableField tf = field.getAnnotation(TableField.class);
        if (tf != null && StrUtil.isNotEmpty(tf.value())) {
            return tf.value();
        }
        TableId ti = field.getAnnotation(TableId.class);
        if (ti != null && StrUtil.isNotEmpty(ti.value())) {
            return ti.value();
        }
        throw BizException.wrap("{}.{} 未标记 @TableField 或 @TableId", clazz.getSimpleName(), beanField);
    }


    /**
     * 替换 实体对象中类型为String 类型的参数，并将% 和 _ 符号转义
     *
     * @param source 源对象
     * @return 最新源对象
     */
    public static <T> T replace(Object source) {
        if (source == null) {
            return null;
        }

        Class<?> srcClass = source.getClass();
        Field[] fields = ReflectUtil.getFields(srcClass);
        for (Field field : fields) {
            Object classValue = ReflectUtil.getFieldValue(source, field);
            if (classValue == null) {
                continue;
            }
            //final 和 static 字段跳过
            if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            if (!(classValue instanceof String srcValue)) {
                continue;
            }
            TableField tableField = AnnotationUtil.getAnnotation(field, TableField.class);
            if (tableField == null) {
                continue;
            }

            String condition = tableField.condition();
            if (StrUtil.isEmpty(condition) || StrUtil.equalsAny(condition, SqlCondition.EQUAL, SqlCondition.NOT_EQUAL)) {
                continue;
            }

            if (srcValue.contains(PERCENT) || srcValue.contains(UNDERSCORE)) {
                String tarValue = StrHelper.keywordConvert(srcValue);
                ReflectUtil.setFieldValue(source, field, tarValue);
            }
        }
        return (T) source;
    }

    /**
     * object 转成集合类，如果非集合返回 null
     *
     * @param obj 对象
     * @return 对应集合
     */
    public static Collection<?> castList(Object obj) {
        return obj instanceof Collection<?> coll ? coll : null;
    }
}
