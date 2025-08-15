package com.luohuo.flex.ai.mapper.model;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeyPageReqVO;
import com.luohuo.flex.ai.dal.model.AiApiKeyDO;
import com.luohuo.flex.ai.mapper.BaseMapperX;
import com.luohuo.flex.ai.mapper.LambdaQueryWrapperX;
import com.luohuo.flex.ai.mapper.QueryWrapperX;
import org.springframework.stereotype.Repository;

/**
 * AI API 密钥 Mapper
 *
 * @author 芋道源码
 */
@Repository
public interface AiApiKeyMapper extends BaseMapperX<AiApiKeyDO> {

    default PageResult<AiApiKeyDO> selectPage(AiApiKeyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiApiKeyDO>()
                .likeIfPresent(AiApiKeyDO::getName, reqVO.getName())
                .eqIfPresent(AiApiKeyDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(AiApiKeyDO::getStatus, reqVO.getStatus())
                .orderByDesc(AiApiKeyDO::getId));
    }

    default AiApiKeyDO selectFirstByPlatformAndStatus(String platform, Integer status) {
        return selectOne(new QueryWrapperX<AiApiKeyDO>()
                .eq("platform", platform)
                .eq("status", status)
                .limitN(1)
                .orderByAsc("id"));
    }

}