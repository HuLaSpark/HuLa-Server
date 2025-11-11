package com.luohuo.flex.ai.mapper.model;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.model.AiModelPageReqVO;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.mapper.BaseMapperX;
import com.luohuo.flex.ai.mapper.LambdaQueryWrapperX;
import com.luohuo.flex.ai.mapper.QueryWrapperX;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.List;

/**
 * API 模型 Mapper
 */
@Repository
public interface AiChatMapper extends BaseMapperX<AiModelDO> {

	default AiModelDO selectFirstByStatus(Integer type, Integer status) {
		return selectOne(new QueryWrapperX<AiModelDO>()
				.eq("type", type)
				.eq("status", status)
				.limitN(1)
				.orderByAsc("sort"));
	}

	default PageResult<AiModelDO> selectPage(AiModelPageReqVO reqVO) {
		return selectPage(reqVO, new LambdaQueryWrapperX<AiModelDO>()
				.likeIfPresent(AiModelDO::getName, reqVO.getName())
				.eqIfPresent(AiModelDO::getModel, reqVO.getModel())
				.eqIfPresent(AiModelDO::getPlatform, reqVO.getPlatform())
				.eqIfPresent(AiModelDO::getPublicStatus, reqVO.getPublicStatus())
				.orderByAsc(AiModelDO::getSort));
	}

	default PageResult<AiModelDO> selectPageByMy(AiModelPageReqVO reqVO, Long userId) {
		LambdaQueryWrapperX<AiModelDO> wrapper = new LambdaQueryWrapperX<AiModelDO>()
				.likeIfPresent(AiModelDO::getName, reqVO.getName())
				.eqIfPresent(AiModelDO::getModel, reqVO.getModel())
				.eqIfPresent(AiModelDO::getPlatform, reqVO.getPlatform());

		// 根据 publicStatus 参数决定查询范围
		if (reqVO.getPublicStatus() == null) {
			// 未指定：返回所有公开模型 + 用户私有模型
			wrapper.and(w -> w
					.eq(AiModelDO::getPublicStatus, 0) // 0=公开
					.or()
					.eq(AiModelDO::getUserId, userId)
			);
		} else if (Integer.valueOf(0).equals(reqVO.getPublicStatus())) {
			// 只查询公开模型
			wrapper.eq(AiModelDO::getPublicStatus, 0);
		} else {
			// 只查询用户私有模型
			wrapper.eq(AiModelDO::getUserId, userId)
					.eq(AiModelDO::getPublicStatus, 1); // 1=私有
		}

		wrapper.orderByAsc(AiModelDO::getSort);
		return selectPage(reqVO, wrapper);
	}

	default List<AiModelDO> selectListByStatusAndType(Integer status, Integer type,
													  @Nullable String platform) {
		return selectList(new LambdaQueryWrapperX<AiModelDO>()
				.eq(AiModelDO::getStatus, status)
				.eq(AiModelDO::getType, type)
				.eqIfPresent(AiModelDO::getPlatform, platform)
				.orderByAsc(AiModelDO::getSort));
	}

	default List<AiModelDO> selectListByStatusAndTypeAndUserId(Integer status, Integer type,
															   @Nullable String platform, Long userId) {
		return selectList(new LambdaQueryWrapperX<AiModelDO>()
				.eq(AiModelDO::getStatus, status)
				.eq(AiModelDO::getType, type)
				.eqIfPresent(AiModelDO::getPlatform, platform)
				.and(wrapper -> wrapper
						// 公开模型（publicStatus=0）
						.eq(AiModelDO::getPublicStatus, 0)
						// 或用户自己的私有模型（publicStatus=1）
						.or()
						.eq(AiModelDO::getUserId, userId)
						.eq(AiModelDO::getPublicStatus, 1)
				)
				.orderByAsc(AiModelDO::getSort));
	}

}
