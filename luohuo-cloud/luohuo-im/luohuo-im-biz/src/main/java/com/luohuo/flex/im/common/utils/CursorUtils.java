package com.luohuo.flex.im.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luohuo.basic.base.LambdaUtils;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.basic.utils.TimeUtils;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 游标分页工具类
 * @author nyh
 */
public class CursorUtils {

    public static <T> CursorPageBaseResp<Pair<T, Double>> getCursorPageByRedis(CursorPageBaseReq cursorPageBaseReq, String redisKey, Function<String, T> typeConvert) {
        Set<ZSetOperations.TypedTuple<Object>> typedTuples;
		CachePlusOps cachePlusOps = SpringUtils.getBean(CachePlusOps.class);
        if (StrUtil.isBlank(cursorPageBaseReq.getCursor())) {
            //第一次
            typedTuples = cachePlusOps.zReverseRangeWithScores(redisKey, cursorPageBaseReq.getPageSize());
        } else {
            typedTuples = cachePlusOps.zReverseRangeByScoreWithScores(redisKey, Double.parseDouble(cursorPageBaseReq.getCursor()), cursorPageBaseReq.getPageSize());
        }
        List<Pair<T, Double>> result = typedTuples
                .stream()
                .map(t -> Pair.of(typeConvert.apply(t.getValue().toString()), t.getScore()))
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toList());
        String cursor = Optional.ofNullable(CollectionUtil.getLast(result))
                .map(Pair::getValue)
                .map(String::valueOf)
                .orElse(null);
        Boolean isLast = result.size() != cursorPageBaseReq.getPageSize();
        return new CursorPageBaseResp<>(cursor, isLast, result, 0L);
    }

    public static <T> CursorPageBaseResp<T> getCursorPageByMysql(IService<T> mapper, CursorPageBaseReq request, Consumer<LambdaQueryWrapper<T>> initWrapper, SFunction<T, ?> cursorColumn) {
        // 游标字段类型
        Class<?> cursorType = LambdaUtils.getReturnType(cursorColumn);
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        // 额外条件
        initWrapper.accept(wrapper);
        // 游标条件
        if (StrUtil.isNotBlank(request.getCursor())) {
            wrapper.lt(cursorColumn, parseCursor(request.getCursor(), cursorType));
        }
        // 游标方向
        wrapper.orderByDesc(cursorColumn);

        Page<T> page = mapper.page(request.plusPage(), wrapper);
        // 取出游标
        String cursor = Optional.ofNullable(CollectionUtil.getLast(page.getRecords()))
                .map(cursorColumn)
                .map(CursorUtils::toCursor)
                .orElse(null);
        // 判断是否最后一页
        Boolean isLast = page.getRecords().size() != request.getPageSize();
        return new CursorPageBaseResp<>(cursor, isLast, page.getRecords(), page.getTotal());
    }

    private static String toCursor(Object o) {
//		if (o instanceof Date) {
//			return String.valueOf(((Date) o).getTime());
//		} else {
		if (o instanceof LocalDateTime) {
			return String.valueOf(TimeUtils.getTime((LocalDateTime) o));
		} else {
            return o.toString();
        }
    }

	private static Object parseCursor(String cursor, Class<?> cursorClass) {
		if (LocalDateTime.class.isAssignableFrom(cursorClass)) {
			try {
				long timestamp = Long.parseLong(cursor);
				return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("时间有问题: " + cursor);
			}
		} else {
			return cursor;
		}
	}
}
