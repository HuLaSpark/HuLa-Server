package com.luohuo.flex.ai.service.model;

import cn.hutool.core.util.ObjectUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeyPageReqVO;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeySaveReqVO;
import com.luohuo.flex.ai.dal.model.AiApiKeyDO;
import com.luohuo.flex.ai.enums.CommonStatusEnum;
import com.luohuo.flex.ai.mapper.model.AiApiKeyMapper;
import com.luohuo.flex.ai.utils.BeanUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.luohuo.flex.ai.enums.ErrorCodeConstants.API_KEY_DISABLE;
import static com.luohuo.flex.ai.enums.ErrorCodeConstants.API_KEY_NOT_EXISTS;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;

/**
 * AI API 密钥 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiApiKeyServiceImpl implements AiApiKeyService {

	@Resource
	private AiApiKeyMapper apiKeyMapper;

	@Override
	public Long createApiKey(AiApiKeySaveReqVO createReqVO, Long userId) {
		// 插入
		AiApiKeyDO apiKey = BeanUtils.toBean(createReqVO, AiApiKeyDO.class);

		// 如果是私有密钥，设置 userId
		if (createReqVO.getPublicStatus() == null || Boolean.FALSE.equals(createReqVO.getPublicStatus())) {
			apiKey.setUserId(userId)
					.setPublicStatus(false);
			// 私有密钥默认启用
			if (createReqVO.getStatus() == null) {
				apiKey.setStatus(CommonStatusEnum.ENABLE.getStatus());
			}
		}

		apiKeyMapper.insert(apiKey);
		return apiKey.getId();
	}

	@Override
	public void updateApiKey(AiApiKeySaveReqVO updateReqVO, Long userId) {
		// 校验存在
		AiApiKeyDO apiKey = validateApiKeyExists(updateReqVO.getId());

		// 权限校验
		if (Boolean.FALSE.equals(apiKey.getPublicStatus())) {
			// 私有密钥，只有创建者可以编辑
			if (ObjectUtil.notEqual(apiKey.getUserId(), userId)) {
				throw exception(API_KEY_NOT_EXISTS);
			}
		} else {
			// 公开密钥，需要管理员权限
			if (userId >= 10937855681025L) {
				throw exception(API_KEY_NOT_EXISTS);
			}
		}

		// 更新
		AiApiKeyDO updateObj = BeanUtils.toBean(updateReqVO, AiApiKeyDO.class);
		apiKeyMapper.updateById(updateObj);
	}

	@Override
	public void deleteApiKey(Long id, Long userId) {
		// 校验存在
		AiApiKeyDO apiKey = validateApiKeyExists(id);

		// 权限校验
		if (Boolean.FALSE.equals(apiKey.getPublicStatus())) {
			// 私有密钥，只有创建者可以删除
			if (ObjectUtil.notEqual(apiKey.getUserId(), userId)) {
				throw exception(API_KEY_NOT_EXISTS);
			}
		} else {
			// 公开密钥，需要管理员权限
			if (userId >= 10937855681025L) {
				throw exception(API_KEY_NOT_EXISTS);
			}
		}

		// 删除
		apiKeyMapper.deleteById(id);
	}

	private AiApiKeyDO validateApiKeyExists(Long id) {
		AiApiKeyDO apiKey = apiKeyMapper.selectById(id);
		if (apiKey == null) {
			throw exception(API_KEY_NOT_EXISTS);
		}
		return apiKey;
	}

	@Override
	public AiApiKeyDO getApiKey(Long id) {
		return apiKeyMapper.selectById(id);
	}

	@Override
	public AiApiKeyDO validateApiKey(Long id) {
		AiApiKeyDO apiKey = validateApiKeyExists(id);
		if (CommonStatusEnum.isDisable(apiKey.getStatus())) {
			throw exception(API_KEY_DISABLE);
		}
		return apiKey;
	}

	@Override
	public PageResult<AiApiKeyDO> getApiKeyPage(AiApiKeyPageReqVO pageReqVO, Long userId) {
		return apiKeyMapper.selectPageByMy(pageReqVO, userId);
	}

	@Override
	public List<AiApiKeyDO> getApiKeyList() {
		return apiKeyMapper.selectList();
	}

	@Override
	public AiApiKeyDO getRequiredDefaultApiKey(String platform, Integer status) {
		AiApiKeyDO apiKey = apiKeyMapper.selectFirstByPlatformAndStatus(platform, status);
		if (apiKey == null) {
			throw exception(API_KEY_NOT_EXISTS);
		}
		return apiKey;
	}

}