package com.luohuo.flex.ai.mapper.model;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.chatRole.AiChatRolePageReqVO;
import com.luohuo.flex.ai.dal.model.AiChatRoleDO;
import com.luohuo.flex.ai.enums.CommonStatusEnum;
import com.luohuo.flex.ai.mapper.BaseMapperX;
import com.luohuo.flex.ai.mapper.LambdaQueryWrapperX;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI 聊天角色 Mapper
 *
 */
@Repository
public interface AiChatRoleMapper extends BaseMapperX<AiChatRoleDO> {

	default PageResult<AiChatRoleDO> selectPage(AiChatRolePageReqVO reqVO) {
		return selectPage(reqVO, new LambdaQueryWrapperX<AiChatRoleDO>()
				.likeIfPresent(AiChatRoleDO::getName, reqVO.getName())
				.eqIfPresent(AiChatRoleDO::getCategory, reqVO.getCategory())
				.eqIfPresent(AiChatRoleDO::getPublicStatus, reqVO.getPublicStatus())
				.orderByAsc(AiChatRoleDO::getSort));
	}

	default PageResult<AiChatRoleDO> selectPageByMy(AiChatRolePageReqVO reqVO, Long userId) {
		LambdaQueryWrapperX<AiChatRoleDO> wrapper = new LambdaQueryWrapperX<AiChatRoleDO>()
				.likeIfPresent(AiChatRoleDO::getName, reqVO.getName())
				.eqIfPresent(AiChatRoleDO::getCategory, reqVO.getCategory());

		// 根据 publicStatus 参数决定查询范围
		if (reqVO.getPublicStatus() == null) {
			// 未指定：返回所有公开角色 + 用户私有角色
			wrapper.and(w -> w
					.eq(AiChatRoleDO::getPublicStatus, true)
					.or()
					.eq(AiChatRoleDO::getUserId, userId)
			);
		} else if (Boolean.TRUE.equals(reqVO.getPublicStatus())) {
			// 只查询公开角色
			wrapper.eq(AiChatRoleDO::getPublicStatus, true);
		} else {
			// 只查询用户私有角色
			wrapper.eq(AiChatRoleDO::getUserId, userId)
					.eq(AiChatRoleDO::getPublicStatus, false);
		}

		wrapper.orderByAsc(AiChatRoleDO::getSort);
		return selectPage(reqVO, wrapper);
	}

	default List<AiChatRoleDO> selectListGroupByCategory(Integer status) {
		return selectList(new LambdaQueryWrapperX<AiChatRoleDO>()
				.select(AiChatRoleDO::getCategory)
				.eq(AiChatRoleDO::getStatus, status)
				.groupBy(AiChatRoleDO::getCategory));
	}

	default List<AiChatRoleDO> selectListByName(String name) {
		return selectList(new LambdaQueryWrapperX<AiChatRoleDO>()
				.likeIfPresent(AiChatRoleDO::getName, name)
				.orderByAsc(AiChatRoleDO::getSort));
	}

}