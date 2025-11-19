package com.luohuo.flex.ai.mapper.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luohuo.basic.utils.collection.CollectionUtils;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.chat.vo.message.AiChatMessagePageReqVO;
import com.luohuo.flex.ai.dal.chat.AiChatMessageDO;
import com.luohuo.flex.ai.mapper.BaseMapperX;
import com.luohuo.flex.ai.mapper.LambdaQueryWrapperX;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AI 聊天对话 Mapper
 */
@Repository
public interface AiChatMessageMapper extends BaseMapperX<AiChatMessageDO> {

    default List<AiChatMessageDO> selectListByConversationId(Long conversationId) {
        return selectList(new LambdaQueryWrapperX<AiChatMessageDO>()
                .eq(AiChatMessageDO::getConversationId, conversationId)
                .orderByAsc(AiChatMessageDO::getId));
    }

    default Map<Long, Integer> selectCountMapByConversationId(Collection<Long> conversationIds) {
        // SQL count 查询
        List<Map<String, Object>> result = selectMaps(new QueryWrapper<AiChatMessageDO>()
                .select("COUNT(id) AS count, conversation_id AS conversationId")
                .in("conversation_id", conversationIds)
                .groupBy("conversation_id"));
        if (CollUtil.isEmpty(result)) {
            return Collections.emptyMap();
        }
        // 转换数据
        return CollectionUtils.convertMap(result,
                record -> MapUtil.getLong(record, "conversationId"),
                record -> MapUtil.getInt(record, "count" ));
    }

    default PageResult<AiChatMessageDO> selectPage(AiChatMessagePageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<AiChatMessageDO>()
                .eqIfPresent(AiChatMessageDO::getConversationId, pageReqVO.getConversationId())
                .eqIfPresent(AiChatMessageDO::getUserId, pageReqVO.getUserId())
                .likeIfPresent(AiChatMessageDO::getContent, pageReqVO.getContent())
                .betweenIfPresent(AiChatMessageDO::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(AiChatMessageDO::getId));
    }

}
