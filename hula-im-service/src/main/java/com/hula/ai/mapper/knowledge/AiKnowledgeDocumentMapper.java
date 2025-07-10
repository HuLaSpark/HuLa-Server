package com.hula.ai.mapper.knowledge;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hula.ai.common.pojo.PageResult;
import com.hula.ai.controller.knowledge.vo.document.AiKnowledgeDocumentPageReqVO;
import com.hula.ai.dal.knowledge.AiKnowledgeDocumentDO;
import com.hula.ai.mapper.BaseMapperX;
import com.hula.ai.mapper.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * AI 知识库文档 Mapper
 *
 */
@Mapper
public interface AiKnowledgeDocumentMapper extends BaseMapperX<AiKnowledgeDocumentDO> {

    default PageResult<AiKnowledgeDocumentDO> selectPage(AiKnowledgeDocumentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiKnowledgeDocumentDO>()
                .eqIfPresent(AiKnowledgeDocumentDO::getKnowledgeId, reqVO.getKnowledgeId())
                .likeIfPresent(AiKnowledgeDocumentDO::getName, reqVO.getName())
                .orderByDesc(AiKnowledgeDocumentDO::getId));
    }

    default void updateRetrievalCountIncr(Collection<Long> ids) {
        update(new LambdaUpdateWrapper<AiKnowledgeDocumentDO>()
                .setSql(" retrieval_count = retrieval_count + 1")
                .in(AiKnowledgeDocumentDO::getId, ids));
    }

    default List<AiKnowledgeDocumentDO> selectListByStatus(Integer status) {
        return selectList(AiKnowledgeDocumentDO::getStatus, status);
    }

    default List<AiKnowledgeDocumentDO> selectListByKnowledgeId(Long knowledgeId) {
        return selectList(AiKnowledgeDocumentDO::getKnowledgeId, knowledgeId);
    }

}
