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
 * @author 乾乾
 */
@Repository
public interface AiApiKeyMapper extends BaseMapperX<AiApiKeyDO> {

	default PageResult<AiApiKeyDO> selectPage(AiApiKeyPageReqVO reqVO) {
		return selectPage(reqVO, new LambdaQueryWrapperX<AiApiKeyDO>()
				.likeIfPresent(AiApiKeyDO::getName, reqVO.getName())
				.eqIfPresent(AiApiKeyDO::getPlatform, reqVO.getPlatform())
				.eqIfPresent(AiApiKeyDO::getStatus, reqVO.getStatus())
				.eqIfPresent(AiApiKeyDO::getPublicStatus, reqVO.getPublicStatus())
				.orderByDesc(AiApiKeyDO::getId));
	}

	default PageResult<AiApiKeyDO> selectPageByMy(AiApiKeyPageReqVO reqVO, Long userId) {
		LambdaQueryWrapperX<AiApiKeyDO> wrapper = new LambdaQueryWrapperX<AiApiKeyDO>()
				.likeIfPresent(AiApiKeyDO::getName, reqVO.getName())
				.eqIfPresent(AiApiKeyDO::getPlatform, reqVO.getPlatform())
				.eqIfPresent(AiApiKeyDO::getStatus, reqVO.getStatus());

		// 根据 publicStatus 参数决定查询范围
		if (reqVO.getPublicStatus() == null) {
			// 未指定：返回所有公开密钥 + 用户私有密钥
			wrapper.and(w -> w
					.eq(AiApiKeyDO::getPublicStatus, true)
					.or()
					.eq(AiApiKeyDO::getUserId, userId)
			);
		} else if (Boolean.TRUE.equals(reqVO.getPublicStatus())) {
			// 只查询公开密钥
			wrapper.eq(AiApiKeyDO::getPublicStatus, true);
		} else {
			// 只查询用户私有密钥
			wrapper.eq(AiApiKeyDO::getUserId, userId)
					.eq(AiApiKeyDO::getPublicStatus, false);
		}

		wrapper.orderByDesc(AiApiKeyDO::getId);
		return selectPage(reqVO, wrapper);
	}

	default AiApiKeyDO selectFirstByPlatformAndStatus(String platform, Integer status) {
		return selectOne(new QueryWrapperX<AiApiKeyDO>()
				.eq("platform", platform)
				.eq("status", status)
				.limitN(1)
				.orderByAsc("id"));
	}

}