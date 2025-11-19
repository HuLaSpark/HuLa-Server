package com.luohuo.flex.ai.mapper.music;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.music.vo.AiMusicPageReqVO;
import com.luohuo.flex.ai.dal.music.AiMusicDO;
import com.luohuo.flex.ai.mapper.BaseMapperX;
import com.luohuo.flex.ai.mapper.LambdaQueryWrapperX;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI 音乐 Mapper
 */
@Repository
public interface AiMusicMapper extends BaseMapperX<AiMusicDO> {

    default List<AiMusicDO> selectListByStatus(Integer status) {
        return selectList(AiMusicDO::getStatus, status);
    }

    default PageResult<AiMusicDO> selectPage(AiMusicPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiMusicDO>()
                .eqIfPresent(AiMusicDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiMusicDO::getTitle, reqVO.getTitle())
                .eqIfPresent(AiMusicDO::getStatus, reqVO.getStatus())
                .eqIfPresent(AiMusicDO::getGenerateMode, reqVO.getGenerateMode())
                .betweenIfPresent(AiMusicDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(AiMusicDO::getPublicStatus, reqVO.getPublicStatus())
                .orderByDesc(AiMusicDO::getId));
    }

    default PageResult<AiMusicDO> selectPageByMy(AiMusicPageReqVO reqVO, Long userId) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiMusicDO>()
                // 情况一：公开
                .eq(Boolean.TRUE.equals(reqVO.getPublicStatus()), AiMusicDO::getPublicStatus, reqVO.getPublicStatus())
                // 情况二：私有
                .eq(Boolean.FALSE.equals(reqVO.getPublicStatus()), AiMusicDO::getUserId, userId)
                .orderByAsc(AiMusicDO::getId));
    }
    
}
