package com.luohuo.flex.ai.service.model;

import com.agentsflex.llm.ollama.OllamaLlm;
import com.agentsflex.llm.ollama.OllamaLlmConfig;
import com.agentsflex.llm.qwen.QwenLlm;
import com.agentsflex.llm.qwen.QwenLlmConfig;
import cn.hutool.core.util.ObjectUtil;
import com.luohuo.basic.constant.Constants;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.model.AiModelPageReqVO;
import com.luohuo.flex.ai.controller.model.vo.model.AiModelSaveMyReqVO;
import com.luohuo.flex.ai.controller.model.vo.model.AiModelSaveReqVO;
import com.luohuo.flex.ai.core.AiModelFactory;
import com.luohuo.flex.ai.core.model.MidjourneyApi;
import com.luohuo.flex.ai.core.model.SunoApi;
import com.luohuo.flex.ai.dal.model.AiApiKeyDO;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.enums.AiPlatformEnum;
import com.luohuo.flex.ai.enums.CommonStatusEnum;
import com.luohuo.flex.ai.mapper.LambdaQueryWrapperX;
import com.luohuo.flex.ai.mapper.model.AiChatMapper;
import com.luohuo.flex.ai.utils.BeanUtils;
import dev.tinyflow.core.Tinyflow;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

import static com.luohuo.flex.ai.enums.ErrorCodeConstants.*;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;

/**
 * AI 模型 Service 实现类
 *
 * @author 乾乾
 */
@Service
@Validated
public class AiModelServiceImpl implements AiModelService {

	@Resource
	private AiApiKeyService apiKeyService;

	@Resource
	private AiChatMapper modelMapper;

	@Resource
	private AiModelFactory modelFactory;

	@Override
	public Long createModel(AiModelSaveReqVO createReqVO) {
		// 1. 校验
		AiPlatformEnum.validatePlatform(createReqVO.getPlatform());
		apiKeyService.validateApiKey(createReqVO.getKeyId());

		// 2. 插入
		AiModelDO model = BeanUtils.toBean(createReqVO, AiModelDO.class);
		if (Integer.valueOf(0).equals(createReqVO.getPublicStatus())) {
			model.setAvatar(Constants.BOT_AVATAR);
		}
		// 如果是私有模型且没有设置 userId，则抛出异常
		if (Integer.valueOf(1).equals(createReqVO.getPublicStatus()) && model.getUserId() == null) {
			throw exception(MODEL_NOT_EXISTS); // 使用适当的错误码
		}
		model.setUserId(ContextUtil.getUid());
		modelMapper.insert(model);
		return model.getId();
	}

	@Override
	public Long createModelMy(AiModelSaveMyReqVO createReqVO, Long userId) {
		// 1. 校验
		AiPlatformEnum.validatePlatform(createReqVO.getPlatform());
		apiKeyService.validateApiKey(createReqVO.getKeyId());

		// 2. 插入
		AiModelDO model = BeanUtils.toBean(createReqVO, AiModelDO.class)
				.setUserId(userId)
				.setStatus(CommonStatusEnum.ENABLE.getStatus())
				.setPublicStatus(1); // 1=私有
		modelMapper.insert(model);
		return model.getId();
	}

	@Override
	public void updateModelMy(AiModelSaveMyReqVO updateReqVO, Long userId) {
		// 1. 校验存在
		AiModelDO model = validateModelExists(updateReqVO.getId());
		if (ObjectUtil.notEqual(model.getUserId(), userId)) {
			throw exception(MODEL_NOT_EXISTS);
		}
		// 2. 校验平台和密钥
		AiPlatformEnum.validatePlatform(updateReqVO.getPlatform());
		apiKeyService.validateApiKey(updateReqVO.getKeyId());

		// 3. 更新
		AiModelDO updateObj = BeanUtils.toBean(updateReqVO, AiModelDO.class);
		if (Integer.valueOf(0).equals(updateReqVO.getPublicStatus())) {
			updateObj.setAvatar(Constants.BOT_AVATAR);
		}
		updateObj.setUserId(userId);
		modelMapper.updateById(updateObj);
	}

	@Override
	public void deleteModel(Long id) {
		// 校验存在
		validateModelExists(id);
		// 删除
		modelMapper.deleteById(id);
	}

	@Override
	public void deleteModelMy(Long id, Long userId) {
		// 校验存在
		AiModelDO model = validateModelExists(id);
		if (ObjectUtil.notEqual(model.getUserId(), userId)) {
			throw exception(MODEL_NOT_EXISTS);
		}
		// 删除
		modelMapper.deleteById(id);
	}

	@Override
	public void updateModelAdmin(AiModelSaveReqVO updateReqVO) {
		// 校验存在
		validateModelExists(updateReqVO.getId());

		// 校验平台和密钥
		AiPlatformEnum.validatePlatform(updateReqVO.getPlatform());
		apiKeyService.validateApiKey(updateReqVO.getKeyId());

		// 管理员更新，无权限校验
		AiModelDO updateObj = BeanUtils.toBean(updateReqVO, AiModelDO.class);
		if (Integer.valueOf(0).equals(updateReqVO.getPublicStatus())) {
			updateObj.setAvatar(Constants.BOT_AVATAR);
		}
		modelMapper.updateById(updateObj);
	}

	@Override
	public void deleteModelAdmin(Long id) {
		// 校验存在
		validateModelExists(id);

		// 管理员删除，无权限校验
		modelMapper.deleteById(id);
	}

	private AiModelDO validateModelExists(Long id) {
		AiModelDO model = modelMapper.selectById(id);
		if (model == null) {
			throw exception(MODEL_NOT_EXISTS);
		}
		return model;
	}

	@Override
	public AiModelDO getModel(Long id) {
		return modelMapper.selectById(id);
	}

	@Override
	public AiModelDO getRequiredDefaultModel(Integer type) {
		AiModelDO model = modelMapper.selectFirstByStatus(type, CommonStatusEnum.ENABLE.getStatus());
		if (model == null) {
			throw exception(MODEL_DEFAULT_NOT_EXISTS);
		}
		return model;
	}

	@Override
	public PageResult<AiModelDO> getModelPage(AiModelPageReqVO pageReqVO) {
		return modelMapper.selectPage(pageReqVO);
	}

	@Override
	public PageResult<AiModelDO> getModelMyPage(AiModelPageReqVO pageReqVO, Long userId) {
		return modelMapper.selectPageByMy(pageReqVO, userId);
	}

	@Override
	public AiModelDO validateModel(Long id) {
		AiModelDO model = validateModelExists(id);
		if (CommonStatusEnum.isDisable(model.getStatus())) {
			throw exception(MODEL_DISABLE);
		}
		return model;
	}

	@Override
	public List<AiModelDO> getModelListByStatusAndType(Integer status, Integer type, String platform) {
		return modelMapper.selectListByStatusAndType(status, type, platform);
	}

	@Override
	public List<AiModelDO> getModelListByStatusAndTypeAndUserId(Integer status, Integer type, String platform, Long userId) {
		return modelMapper.selectListByStatusAndTypeAndUserId(status, type, platform, userId);
	}

	@Override
	public List<AiModelDO> getAllModelList() {
		return modelMapper.selectList(new LambdaQueryWrapperX<AiModelDO>()
				.orderByDesc(AiModelDO::getId));
	}

	// ========== 与 Spring AI 集成 ==========

	@Override
	public ChatModel getChatModel(Long id) {
		AiModelDO model = validateModel(id);
		AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
		AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
		return modelFactory.getOrCreateChatModel(platform, apiKey.getApiKey(), apiKey.getUrl());
	}

	@Override
	public ImageModel getImageModel(Long id) {
		AiModelDO model = validateModel(id);
		AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
		AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
		return modelFactory.getOrCreateImageModel(platform, apiKey.getApiKey(), apiKey.getUrl());
	}

	@Override
	public com.luohuo.flex.ai.core.model.video.VideoModel getVideoModel(Long id) {
		AiModelDO model = validateModel(id);
		AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
		AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
		return modelFactory.getOrCreateVideoModel(platform, apiKey.getApiKey(), apiKey.getUrl());
	}

	@Override
	public com.luohuo.flex.ai.core.model.audio.AudioModel getAudioModel(Long id) {
		AiModelDO model = validateModel(id);
		AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
		AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
		return modelFactory.getOrCreateAudioModel(platform, apiKey.getApiKey(), apiKey.getUrl());
	}

	@Override
	public MidjourneyApi getMidjourneyApi(Long id) {
		AiModelDO model = validateModel(id);
		AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
		return modelFactory.getOrCreateMidjourneyApi(apiKey.getApiKey(), apiKey.getUrl());
	}

	@Override
	public SunoApi getSunoApi() {
		AiApiKeyDO apiKey = apiKeyService.getRequiredDefaultApiKey(
				AiPlatformEnum.SUNO.getPlatform(), CommonStatusEnum.ENABLE.getStatus());
		return modelFactory.getOrCreateSunoApi(apiKey.getApiKey(), apiKey.getUrl());
	}

	@Override
	public VectorStore getOrCreateVectorStore(Long id, Map<String, Class<?>> metadataFields) {
		// 获取模型 + 密钥
		AiModelDO model = validateModel(id);
		AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
		AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());

		// 创建或获取 EmbeddingModel 对象
		EmbeddingModel embeddingModel = modelFactory.getOrCreateEmbeddingModel(
				platform, apiKey.getApiKey(), apiKey.getUrl(), model.getModel());

		// 创建或获取 VectorStore 对象
		return modelFactory.getOrCreateVectorStore(SimpleVectorStore.class, embeddingModel, metadataFields);
//         return modelFactory.getOrCreateVectorStore(QdrantVectorStore.class, embeddingModel, metadataFields);
//         return modelFactory.getOrCreateVectorStore(RedisVectorStore.class, embeddingModel, metadataFields);
//         return modelFactory.getOrCreateVectorStore(MilvusVectorStore.class, embeddingModel, metadataFields);
	}

	// TODO @lesan：是不是返回 Llm 对象会好点哈？
	@Override
	public void getLLmProvider4Tinyflow(Tinyflow tinyflow, Long modelId) {
		AiModelDO model = validateModel(modelId);
		AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
		AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
		switch (platform) {
			// TODO @lesan 考虑到未来不需要使用agents-flex 现在仅测试通义千问
			// TODO @lesan：【重要】是不是可以实现一个 SpringAiLlm，这样的话，内部全部用它就好了。只实现 chat 部分；这样，就把 flex 作为一个 agent 框架，内部调用，还是 spring ai 相关的。成本可能低一点？！
			case TONG_YI:
				QwenLlmConfig qwenLlmConfig = new QwenLlmConfig();
				qwenLlmConfig.setApiKey(apiKey.getApiKey());
				qwenLlmConfig.setModel(model.getModel());
				// TODO @lesan：这个有点奇怪。。。如果一个链式里，有多个模型，咋整呀。。。
				tinyflow.setLlmProvider(id -> new QwenLlm(qwenLlmConfig));
				break;
			case OLLAMA:
				OllamaLlmConfig ollamaLlmConfig = new OllamaLlmConfig();
				ollamaLlmConfig.setEndpoint(apiKey.getUrl());
				ollamaLlmConfig.setModel(model.getModel());
				tinyflow.setLlmProvider(id -> new OllamaLlm(ollamaLlmConfig));
				break;
		}
	}

}