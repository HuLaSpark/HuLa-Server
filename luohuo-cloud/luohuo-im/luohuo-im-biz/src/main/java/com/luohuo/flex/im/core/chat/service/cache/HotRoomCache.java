package com.luohuo.flex.im.core.chat.service.cache;

import cn.hutool.core.lang.Pair;
import com.luohuo.basic.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.flex.im.common.constant.RedisKey;
import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import com.luohuo.flex.im.domain.vo.res.CursorPageBaseResp;
import com.luohuo.flex.im.common.utils.CursorUtils;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 全局房间
 * @author nyh
 */
@Component
@RequiredArgsConstructor
public class HotRoomCache {

	private final CachePlusOps cachePlusOps;
    /**
     * 获取热门群聊翻页
     *
     * @return
     */
    public CursorPageBaseResp<Pair<Long, Double>> getRoomCursorPage(CursorPageBaseReq pageBaseReq) {
        return CursorUtils.getCursorPageByRedis(pageBaseReq, RedisKey.getKey(RedisKey.HOT_ROOM_ZET), Long::parseLong);
    }

    public Set<ZSetOperations.TypedTuple<Object>> getRoomRange(Double hotStart, Double hotEnd) {
        return cachePlusOps.zRangeByScoreWithScores(RedisKey.getKey(RedisKey.HOT_ROOM_ZET), hotStart, hotEnd);
    }

    /**
     * 更新热门群聊的最新时间
     */
    public void refreshActiveTime(Long roomId, LocalDateTime refreshTime) {
		cachePlusOps.zAdd(RedisKey.getKey(RedisKey.HOT_ROOM_ZET), roomId, (double) TimeUtils.getTime(refreshTime));
    }
}
