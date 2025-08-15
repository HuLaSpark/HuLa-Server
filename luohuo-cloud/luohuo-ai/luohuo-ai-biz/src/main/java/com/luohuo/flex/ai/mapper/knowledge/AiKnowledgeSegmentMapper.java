package com.luohuo.flex.ai.mapper.knowledge;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.knowledge.vo.segment.AiKnowledgeSegmentPageReqVO;
import com.luohuo.flex.ai.controller.knowledge.vo.segment.AiKnowledgeSegmentProcessRespVO;
import com.luohuo.flex.ai.dal.knowledge.AiKnowledgeSegmentDO;
import com.luohuo.flex.ai.mapper.BaseMapperX;
import com.luohuo.flex.ai.mapper.LambdaQueryWrapperX;
import com.luohuo.flex.ai.mapper.MPJLambdaWrapperX;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * AI 知识库分片 Mapper
 *
 * @author xiaoxin
 */
@Repository
public interface AiKnowledgeSegmentMapper extends BaseMapperX<AiKnowledgeSegmentDO> {

    default PageResult<AiKnowledgeSegmentDO> selectPage(AiKnowledgeSegmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiKnowledgeSegmentDO>()
                .eq(AiKnowledgeSegmentDO::getDocumentId, reqVO.getDocumentId())
                .likeIfPresent(AiKnowledgeSegmentDO::getContent, reqVO.getContent())
                .eqIfPresent(AiKnowledgeSegmentDO::getStatus, reqVO.getStatus())
                .orderByDesc(AiKnowledgeSegmentDO::getId));
    }

    default List<AiKnowledgeSegmentDO> selectListByVectorIds(List<String> vectorIds) {
        return selectList(new LambdaQueryWrapperX<AiKnowledgeSegmentDO>()
                .in(AiKnowledgeSegmentDO::getVectorId, vectorIds)
                .orderByDesc(AiKnowledgeSegmentDO::getId));
    }

    default List<AiKnowledgeSegmentDO> selectListByDocumentId(Long documentId) {
        return selectList(new LambdaQueryWrapperX<AiKnowledgeSegmentDO>()
                .eq(AiKnowledgeSegmentDO::getDocumentId, documentId)
                .orderByDesc(AiKnowledgeSegmentDO::getId));
    }

    default List<AiKnowledgeSegmentDO> selectListByKnowledgeIdAndStatus(Long knowledgeId, Integer status) {
        return selectList(AiKnowledgeSegmentDO::getKnowledgeId, knowledgeId,
                AiKnowledgeSegmentDO::getStatus, status);
    }

    default List<AiKnowledgeSegmentProcessRespVO> selectProcessList(Collection<Long> documentIds) {
        MPJLambdaWrapper<AiKnowledgeSegmentDO> wrapper = new MPJLambdaWrapperX<AiKnowledgeSegmentDO>()
                .selectAs(AiKnowledgeSegmentDO::getDocumentId, AiKnowledgeSegmentProcessRespVO::getDocumentId)
                .selectCount(AiKnowledgeSegmentDO::getId, "count")
                .select("COUNT(CASE WHEN vector_id > '" + AiKnowledgeSegmentDO.VECTOR_ID_EMPTY
                        + "' THEN 1 ELSE NULL END) AS embeddingCount")
                .in(AiKnowledgeSegmentDO::getDocumentId, documentIds)
                .groupBy(AiKnowledgeSegmentDO::getDocumentId);
        return selectJoinList(AiKnowledgeSegmentProcessRespVO.class, wrapper);
    }

    default void updateRetrievalCountIncrByIds(List<Long> ids) {
        update(new LambdaUpdateWrapper<AiKnowledgeSegmentDO>()
                .setSql(" retrieval_count = retrieval_count + 1")
                .in(AiKnowledgeSegmentDO::getId, ids));
    }

}
