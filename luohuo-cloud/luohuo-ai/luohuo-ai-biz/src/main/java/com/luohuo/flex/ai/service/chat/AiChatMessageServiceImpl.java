package com.luohuo.flex.ai.service.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.chat.vo.conversation.AiDelReqVO;
import com.luohuo.flex.ai.controller.chat.vo.conversation.AiChatConversationUpdateMyReqVO;
import com.luohuo.flex.ai.controller.chat.vo.message.AiChatMessagePageReqVO;
import com.luohuo.flex.ai.controller.chat.vo.message.AiChatMessageRespVO;
import com.luohuo.flex.ai.controller.chat.vo.message.AiChatMessageSendReqVO;
import com.luohuo.flex.ai.controller.chat.vo.message.AiChatMessageSendRespVO;

import com.luohuo.flex.ai.core.model.strategy.chat.ChatCallStrategy;
import com.luohuo.flex.ai.core.model.strategy.chat.ChatStrategyFactory;
import com.luohuo.flex.ai.core.model.strategy.chat.ChatStreamingStrategy;
import com.luohuo.flex.ai.core.model.strategy.chat.ReasoningChunk;
import com.luohuo.flex.ai.dal.chat.AiChatConversationDO;
import com.luohuo.flex.ai.dal.chat.AiChatMessageDO;
import com.luohuo.flex.ai.dal.knowledge.AiKnowledgeDocumentDO;
import com.luohuo.flex.ai.dal.model.AiChatRoleDO;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.dal.model.AiToolDO;
import com.luohuo.flex.ai.enums.AiChatMessageContentTypeEnum;
import com.luohuo.flex.ai.enums.AiPlatformEnum;
import com.luohuo.flex.ai.enums.ErrorCodeConstants;
import com.luohuo.flex.ai.mapper.chat.AiChatMessageMapper;
import com.luohuo.flex.ai.service.knowledge.AiKnowledgeDocumentService;
import com.luohuo.flex.ai.service.knowledge.AiKnowledgeSegmentService;
import com.luohuo.flex.ai.service.knowledge.bo.AiKnowledgeSegmentSearchReqBO;
import com.luohuo.flex.ai.service.knowledge.bo.AiKnowledgeSegmentSearchRespBO;
import com.luohuo.flex.ai.service.model.AiChatRoleService;
import com.luohuo.flex.ai.service.model.AiModelService;
import com.luohuo.flex.ai.service.model.AiModelUsageService;
import com.luohuo.flex.ai.service.model.AiApiKeyService;
import com.luohuo.flex.ai.dal.model.AiApiKeyDO;

import com.luohuo.flex.ai.core.model.openrouter.OpenRouterApiConstants;
import com.luohuo.flex.ai.core.model.silicon.SiliconFlowApiConstants;
import com.luohuo.flex.ai.service.model.AiToolService;
import com.luohuo.flex.ai.utils.AiUtils;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.basic.base.R;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.StreamingChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.luohuo.basic.utils.collection.CollectionUtils.convertSet;
import static com.luohuo.flex.ai.common.pojo.CommonResult.error;
import static com.luohuo.basic.base.R.success;
import static com.luohuo.flex.ai.enums.ErrorCodeConstants.CHAT_CONVERSATION_NOT_EXISTS;
import static com.luohuo.flex.ai.enums.ErrorCodeConstants.CHAT_MESSAGE_NOT_EXIST;
import static com.luohuo.flex.ai.enums.ErrorCodeConstants.CHAT_PROMPT_TOO_LONG;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;
import static com.luohuo.basic.utils.collection.CollectionUtils.convertList;

/**
 * AI 聊天消息 Service 实现类
 *
 * @author 乾乾
 */
@Service
@Slf4j
public class AiChatMessageServiceImpl implements AiChatMessageService {

	/**
	 * 知识库转 {@link UserMessage} 的内容模版
	 */
	private static final String KNOWLEDGE_USER_MESSAGE_TEMPLATE = "使用 <Reference></Reference> 标记中的内容作为本次对话的参考:\n\n" +
			"%s\n\n" + // 多个 <Reference></Reference> 的拼接
			"回答要求：\n- 避免提及你是从 <Reference></Reference> 获取的知识。";

	@Resource
	private AiChatMessageMapper chatMessageMapper;

	@Resource
	private AiChatConversationService chatConversationService;
	@Resource
	private AiChatRoleService chatRoleService;
	@Resource
	private AiModelService modalService;
	@Resource
	private AiKnowledgeSegmentService knowledgeSegmentService;
	@Resource
	private AiKnowledgeDocumentService knowledgeDocumentService;
	@Resource
	private AiToolService toolService;
	@Resource
	private AiModelUsageService modelUsageService;
	@Resource
	private AiApiKeyService apiKeyService;

	@Transactional(rollbackFor = Exception.class)
	public AiChatMessageSendRespVO sendMessage(AiChatMessageSendReqVO sendReqVO, Long userId) {
		// 1.1 校验对话存在
		AiChatConversationDO conversation = chatConversationService
				.validateChatConversationExists(sendReqVO.getConversationId());
		if (ObjUtil.notEqual(conversation.getUserId(), userId)) {
			throw exception(CHAT_CONVERSATION_NOT_EXISTS);
		}
		List<AiChatMessageDO> historyMessages = chatMessageMapper.selectListByConversationId(conversation.getId());
		// 1.2 校验模型
		AiModelDO model = modalService.validateModel(conversation.getModelId());

		// 1.2.1 会话 Token 预算校验：累计 tokenUsage 超过模型 maxTokens 则拒绝
		if (model.getMaxTokens() != null && conversation.getTokenUsage() != null
				&& conversation.getTokenUsage() >= model.getMaxTokens()) {
			throw exception(ErrorCodeConstants.CHAT_TOKEN_BUDGET_EXCEEDED);
		}

		// 1.3 检查并扣减模型使用次数
		modelUsageService.checkAndDeductUsage(userId, model);

		ChatModel chatModel = modalService.getChatModel(model.getId());

		// 2. 知识库找回
		List<AiKnowledgeSegmentSearchRespBO> knowledgeSegments = recallKnowledgeSegment(sendReqVO.getContent(), conversation);

		// 3. 插入 user 发送消息
		AiChatMessageDO userMessage = createChatMessage(conversation.getId(), null, model,
				userId, conversation.getRoleId(), MessageType.USER, sendReqVO.getContent(), sendReqVO.getUseContext(),
				null);

		// 3.1 插入 assistant 接收消息
		AiChatMessageDO assistantMessage = createChatMessage(conversation.getId(), userMessage.getId(), model,
				userId, conversation.getRoleId(), MessageType.ASSISTANT, "", sendReqVO.getUseContext(),
				knowledgeSegments);

		// 3.2 创建 chat 需要的 Prompt
		Prompt prompt = buildPrompt(conversation, historyMessages, knowledgeSegments, model, sendReqVO);
		AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
		String answerContent;
		String reasoningContent;
		ChatCallStrategy callStrategy = ChatStrategyFactory.getCallStrategy(platform);
		if (callStrategy != null) {
			AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
			String baseUrl = ChatStrategyFactory.resolveBaseUrl(platform, apiKey.getUrl());
			String effectiveModel = ChatStrategyFactory.normalizeModel(platform, model.getModel(), sendReqVO.getReasoningEnabled());
			List<Message> instructions = prompt.getInstructions();
			List<Map<String, String>> messages = new ArrayList<>();
			for (Message m : instructions) {
				String role;
				if (m instanceof SystemMessage) role = "system"; else if (m instanceof UserMessage) role = "user"; else role = "assistant";
				Map<String, String> item = Map.of("role", role, "content", m.getText());
				messages.add(item);
			}
			try {
				ChatCallStrategy.Result result = callStrategy.call(baseUrl, apiKey.getApiKey(), effectiveModel, messages, conversation.getMaxTokens(), conversation.getTemperature());
				answerContent = stripReasoningTags(StrUtil.nullToDefault(result.content, ""));
				reasoningContent = Boolean.TRUE.equals(sendReqVO.getReasoningEnabled()) ? StrUtil.nullToDefault(result.reasoning, "") : "";
			} catch (Exception e) {
				ChatResponse chatResponse = chatModel.call(prompt);
				String rawContentFallback = chatResponse.getResult().getOutput().getText();
				answerContent = stripReasoningTags(StrUtil.nullToDefault(rawContentFallback, ""));
				reasoningContent = Boolean.TRUE.equals(sendReqVO.getReasoningEnabled()) ? extractReasoningFromText(rawContentFallback) : null;
			}
		} else {
			ChatResponse chatResponse = chatModel.call(prompt);
			String rawContent = chatResponse.getResult().getOutput().getText();
			answerContent = stripReasoningTags(StrUtil.nullToDefault(rawContent, ""));
			reasoningContent = Boolean.TRUE.equals(sendReqVO.getReasoningEnabled()) ? extractReasoningFromText(rawContent) : null;
		}
		AiChatMessageDO updateMessage = new AiChatMessageDO().setId(assistantMessage.getId()).setContent(answerContent);
		if (Boolean.TRUE.equals(sendReqVO.getReasoningEnabled()) && StrUtil.isNotEmpty(reasoningContent)) {
			updateMessage.setReasoningContent(reasoningContent);
		}
		chatMessageMapper.updateById(updateMessage);
		// 用量统计：prompt + completion
		int promptTokens = estimateContextTokens(conversation, historyMessages, sendReqVO.getContent());
		int completionTokens = estimateTokens(answerContent);
		int newTotal = (conversation.getTokenUsage() == null ? 0 : conversation.getTokenUsage()) + promptTokens + completionTokens;
		AiChatConversationUpdateMyReqVO updateReq = new AiChatConversationUpdateMyReqVO();
		updateReq.setId(conversation.getId());
		updateReq.setTokenUsage(newTotal);
		chatConversationService.updateChatConversationMy(updateReq, userId);
		// 3.4 响应结果
		Map<Long, AiKnowledgeDocumentDO> documentMap = knowledgeDocumentService.getKnowledgeDocumentMap(
				convertSet(knowledgeSegments, AiKnowledgeSegmentSearchRespBO::getDocumentId));
		List<AiChatMessageRespVO.KnowledgeSegment> segments = BeanUtils.toBean(knowledgeSegments,
				AiChatMessageRespVO.KnowledgeSegment.class, segment -> {
					AiKnowledgeDocumentDO document = documentMap.get(segment.getDocumentId());
					segment.setDocumentName(document != null ? document.getName() : null);
				});
		return new AiChatMessageSendRespVO()
				.setSend(BeanUtils.toBean(userMessage, AiChatMessageSendRespVO.Message.class))
				.setReceive(BeanUtils.toBean(assistantMessage, AiChatMessageSendRespVO.Message.class)
						.setContent(answerContent)
						.setReasoningContent(reasoningContent)
						.setSegments(segments));
	}

	@Override
	public Flux<R<AiChatMessageSendRespVO>> sendChatMessageStream(AiChatMessageSendReqVO sendReqVO, Long userId) {
		ContextUtil.setIgnore(true);

		// 1.1 校验对话存在
		AiChatConversationDO conversation = chatConversationService
				.validateChatConversationExists(sendReqVO.getConversationId());
		if (ObjUtil.notEqual(conversation.getUserId(), userId)) {
			throw exception(CHAT_CONVERSATION_NOT_EXISTS);
		}
		List<AiChatMessageDO> historyMessages = chatMessageMapper.selectListByConversationId(conversation.getId());
		// 1.2 校验模型
		AiModelDO model = modalService.validateModel(conversation.getModelId());

		// 1.3 检查并扣减模型使用次数
		modelUsageService.checkAndDeductUsage(userId, model);

		StreamingChatModel chatModel = modalService.getChatModel(model.getId());

		// 1.2.1 会话 Token 预算校验：累计 tokenUsage 超过模型 maxTokens 则拒绝
		if (model.getMaxTokens() != null && conversation.getTokenUsage() != null
				&& conversation.getTokenUsage() >= model.getMaxTokens()) {
			return Flux.just(error(ErrorCodeConstants.CHAT_TOKEN_BUDGET_EXCEEDED));
		}

		// 2. 知识库找回
		List<AiKnowledgeSegmentSearchRespBO> knowledgeSegments = recallKnowledgeSegment(sendReqVO.getContent(),
				conversation);

		// 3. 插入 user 发送消息
		AiChatMessageDO userMessage = createChatMessage(conversation.getId(), null, model,
				userId, conversation.getRoleId(), MessageType.USER, sendReqVO.getContent(), sendReqVO.getUseContext(),
				null);

		// 4.1 插入 assistant 接收消息
		AiChatMessageDO assistantMessage = createChatMessage(conversation.getId(), userMessage.getId(), model,
				userId, conversation.getRoleId(), MessageType.ASSISTANT, "", sendReqVO.getUseContext(),
				knowledgeSegments);

		// 4.2 构建 Prompt，并进行调用
		Prompt prompt = buildPrompt(conversation, historyMessages, knowledgeSegments, model, sendReqVO);

		AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
		ChatStreamingStrategy streamingStrategy = ChatStrategyFactory.getStreamingStrategy(platform);
		if (streamingStrategy != null) {
			StringBuffer contentBuffer = new StringBuffer();
			StringBuffer reasoningBuffer = new StringBuffer();
			AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
			String baseUrl = ChatStrategyFactory.resolveBaseUrl(platform, apiKey.getUrl());
			String effectiveModel = ChatStrategyFactory.normalizeModel(platform, model.getModel(), sendReqVO.getReasoningEnabled());
			List<Message> instructions = prompt.getInstructions();
			List<Map<String, String>> messages = new ArrayList<>();
			for (Message m : instructions) {
				String role;
				if (m instanceof SystemMessage) role = "system"; else if (m instanceof UserMessage) role = "user"; else role = "assistant";
				Map<String, String> item = Map.of("role", role, "content", m.getText());
				messages.add(item);
			}
			Flux<ReasoningChunk> flux = streamingStrategy.stream(baseUrl, apiKey.getApiKey(), effectiveModel, messages, conversation.getMaxTokens(), conversation.getTemperature());
			final boolean reasoningOn = sendReqVO.getReasoningEnabled();
			return flux.map(obj -> {
						String incremental = StrUtil.nullToDefault(obj.content, "");
						String reasoningInc = reasoningOn ? StrUtil.nullToDefault(obj.reasoning, "") : "";
						List<AiChatMessageRespVO.KnowledgeSegment> segments = null;
						if (StrUtil.isEmpty(contentBuffer)) {
							Map<Long, AiKnowledgeDocumentDO> documentMap = knowledgeDocumentService.getKnowledgeDocumentMap(
									convertSet(knowledgeSegments, AiKnowledgeSegmentSearchRespBO::getDocumentId));
							segments = BeanUtils.toBean(knowledgeSegments, AiChatMessageRespVO.KnowledgeSegment.class, segment -> {
								AiKnowledgeDocumentDO document = documentMap.get(segment.getDocumentId());
								segment.setDocumentName(document != null ? document.getName() : null);
							});
						}
						String answerChunk = stripReasoningTags(incremental);
						contentBuffer.append(answerChunk);
						String newReasoningContent = reasoningInc;
						if (reasoningOn && StrUtil.isNotEmpty(newReasoningContent)) reasoningBuffer.append(newReasoningContent);
						return success(new AiChatMessageSendRespVO()
								.setSend(BeanUtils.toBean(userMessage, AiChatMessageSendRespVO.Message.class))
								.setReceive(BeanUtils.toBean(assistantMessage, AiChatMessageSendRespVO.Message.class)
										.setContent(answerChunk)
										.setReasoningContent(reasoningOn && StrUtil.isNotEmpty(newReasoningContent) ? newReasoningContent : null)
										.setSegments(segments)));
					}).doOnComplete(() -> {
						try {
							ContextUtil.setIgnore(true);
							String content = stripReasoningTags(contentBuffer.toString());
							String reasoningContent = reasoningBuffer.toString();
							AiChatMessageDO updateMessage = new AiChatMessageDO().setId(assistantMessage.getId()).setContent(content);
							if (reasoningOn && StrUtil.isNotEmpty(reasoningContent)) {
								updateMessage.setReasoningContent(reasoningContent);
							}
							chatMessageMapper.updateById(updateMessage);
							int promptTokens = estimateContextTokens(conversation, historyMessages, sendReqVO.getContent());
							int completionTokens = estimateTokens(content);
							int newTotal = (conversation.getTokenUsage() == null ? 0 : conversation.getTokenUsage()) + promptTokens + completionTokens;
							AiChatConversationUpdateMyReqVO streamUpdateReq = new AiChatConversationUpdateMyReqVO();
							streamUpdateReq.setId(conversation.getId());
							streamUpdateReq.setTokenUsage(newTotal);
							chatConversationService.updateChatConversationMy(streamUpdateReq, conversation.getUserId());
						} finally {
							ContextUtil.setIgnore(false);
						}
					}).doOnError(throwable -> log.error("[sendChatMessageStream][userId({}) sendReqVO({}) 发生异常]", userId, sendReqVO, throwable))
					.onErrorResume(error -> {
						Flux<ChatResponse> streamResponse = chatModel.stream(prompt);
						StringBuffer fbContentBuffer = new StringBuffer();
						StringBuffer fbReasoningBuffer = new StringBuffer();
						return streamResponse.map(chunk -> {
							List<AiChatMessageRespVO.KnowledgeSegment> segments = null;
							if (StrUtil.isEmpty(fbContentBuffer)) {
								Map<Long, AiKnowledgeDocumentDO> documentMap = knowledgeDocumentService.getKnowledgeDocumentMap(
										convertSet(knowledgeSegments, AiKnowledgeSegmentSearchRespBO::getDocumentId));
								segments = BeanUtils.toBean(knowledgeSegments, AiChatMessageRespVO.KnowledgeSegment.class, segment -> {
									AiKnowledgeDocumentDO document = documentMap.get(segment.getDocumentId());
									segment.setDocumentName(document != null ? document.getName() : null);
								});
							}
							String newContent = chunk.getResult() != null ? chunk.getResult().getOutput().getText() : null;
							newContent = StrUtil.nullToDefault(newContent, "");
							String answerChunk = stripReasoningTags(newContent);
							fbContentBuffer.append(answerChunk);
							String newReasoningContent = null;
							if (reasoningOn && chunk.getMetadata() != null) {
								String metaReason = extractReasoningFromMetadata(chunk.getMetadata());
								if (StrUtil.isNotEmpty(metaReason)) {
									newReasoningContent = metaReason;
									fbReasoningBuffer.append(metaReason);
								} else {
									String extracted = extractReasoningFromText(newContent);
									if (StrUtil.isNotEmpty(extracted)) {
										newReasoningContent = extracted;
										fbReasoningBuffer.append(extracted);
									}
								}
							} else if (reasoningOn) {
								String extracted = extractReasoningFromText(newContent);
								if (StrUtil.isNotEmpty(extracted)) {
									newReasoningContent = extracted;
									fbReasoningBuffer.append(extracted);
								}
							}
							return success(new AiChatMessageSendRespVO()
									.setSend(BeanUtils.toBean(userMessage, AiChatMessageSendRespVO.Message.class))
									.setReceive(BeanUtils.toBean(assistantMessage, AiChatMessageSendRespVO.Message.class)
											.setContent(answerChunk)
											.setReasoningContent(reasoningOn ? newReasoningContent : null)
											.setSegments(segments)));
						}).doOnComplete(() -> {
							try {
								ContextUtil.setIgnore(true);
								String content = stripReasoningTags(fbContentBuffer.toString());
								String reasoningContent = fbReasoningBuffer.toString();
								AiChatMessageDO updateMessage = new AiChatMessageDO().setId(assistantMessage.getId()).setContent(content);
								if (reasoningOn && StrUtil.isNotEmpty(reasoningContent)) {
									updateMessage.setReasoningContent(reasoningContent);
								}
								chatMessageMapper.updateById(updateMessage);
								int promptTokens = estimateContextTokens(conversation, historyMessages, sendReqVO.getContent());
								int completionTokens = estimateTokens(content);
								int newTotal = (conversation.getTokenUsage() == null ? 0 : conversation.getTokenUsage()) + promptTokens + completionTokens;
								AiChatConversationUpdateMyReqVO streamUpdateReq = new AiChatConversationUpdateMyReqVO();
								streamUpdateReq.setId(conversation.getId());
								streamUpdateReq.setTokenUsage(newTotal);
								chatConversationService.updateChatConversationMy(streamUpdateReq, conversation.getUserId());
							} finally {
								ContextUtil.setIgnore(false);
							}
						});
					});
		}

		Flux<ChatResponse> streamResponse = chatModel.stream(prompt);

		StringBuffer contentBuffer = new StringBuffer();
		StringBuffer reasoningBuffer = new StringBuffer();
		return streamResponse.map(chunk -> {
					// 处理知识库的返回，只有首次才有
					List<AiChatMessageRespVO.KnowledgeSegment> segments = null;
					if (StrUtil.isEmpty(contentBuffer)) {
						Map<Long, AiKnowledgeDocumentDO> documentMap = knowledgeDocumentService.getKnowledgeDocumentMap(
								convertSet(knowledgeSegments, AiKnowledgeSegmentSearchRespBO::getDocumentId));
						segments = BeanUtils.toBean(knowledgeSegments, AiChatMessageRespVO.KnowledgeSegment.class, segment -> {
							AiKnowledgeDocumentDO document = documentMap.get(segment.getDocumentId());
							segment.setDocumentName(document != null ? document.getName() : null);
						});
					}

					// 响应结果
					String newContent = chunk.getResult() != null ? chunk.getResult().getOutput().getText() : null;
					newContent = StrUtil.nullToDefault(newContent, "");
					String answerChunk = stripReasoningTags(newContent);
					contentBuffer.append(answerChunk);

					// 提取推理内容
					String newReasoningContent = null;
					if (chunk.getMetadata() != null) {
						String metaReason = extractReasoningFromMetadata(chunk.getMetadata());
						if (StrUtil.isNotEmpty(metaReason)) {
							newReasoningContent = metaReason;
							reasoningBuffer.append(metaReason);
						} else {
							String extracted = extractReasoningFromText(newContent);
							if (StrUtil.isNotEmpty(extracted)) {
								newReasoningContent = extracted;
								reasoningBuffer.append(extracted);
							}
						}
					} else {
						String extracted = extractReasoningFromText(newContent);
						if (StrUtil.isNotEmpty(extracted)) {
							newReasoningContent = extracted;
							reasoningBuffer.append(extracted);
						}
					}

					return success(new AiChatMessageSendRespVO()
							.setSend(BeanUtils.toBean(userMessage, AiChatMessageSendRespVO.Message.class))
							.setReceive(BeanUtils.toBean(assistantMessage, AiChatMessageSendRespVO.Message.class)
									.setContent(answerChunk)
									.setReasoningContent(newReasoningContent)
									.setSegments(segments)));
				}).doOnComplete(() -> {
					// 手动设置租户信息（因为 Flux 异步会切换线程，导致 ThreadLocal 丢失）
					try {
						ContextUtil.setIgnore(true);

						String content = stripReasoningTags(contentBuffer.toString());
						String reasoningContent = reasoningBuffer.toString();
						AiChatMessageDO updateMessage = new AiChatMessageDO().setId(assistantMessage.getId()).setContent(content);
						if (StrUtil.isNotEmpty(reasoningContent)) {
							updateMessage.setReasoningContent(reasoningContent);
						}
						chatMessageMapper.updateById(updateMessage);
						// 用量统计：prompt + completion（流式完成时）
						int promptTokens = estimateContextTokens(conversation, historyMessages, sendReqVO.getContent());
						int completionTokens = estimateTokens(content);
						int newTotal = (conversation.getTokenUsage() == null ? 0 : conversation.getTokenUsage()) + promptTokens + completionTokens;
						AiChatConversationUpdateMyReqVO streamUpdateReq = new AiChatConversationUpdateMyReqVO();
						streamUpdateReq.setId(conversation.getId());
						streamUpdateReq.setTokenUsage(newTotal);
						chatConversationService.updateChatConversationMy(streamUpdateReq, conversation.getUserId());
					} finally {
						ContextUtil.setIgnore(false);
					}
				}).doOnError(throwable -> log.error("[sendChatMessageStream][userId({}) sendReqVO({}) 发生异常]", userId, sendReqVO, throwable))
				.onErrorResume(error -> Flux.just(error(ErrorCodeConstants.CHAT_STREAM_ERROR)));
	}

	private String extractReasoningFromMetadata(ChatResponseMetadata metadata) {
		if (metadata == null) return null;
		String[] keys = new String[]{
				"reasoning_content",
				"reasoning",
				"thinking",
				"deliberate_output",
				"thoughts"
		};
		for (String k : keys) {
			Object v = metadata.get(k);
			if (v != null) {
				String s = String.valueOf(v);
				if (StrUtil.isNotEmpty(s)) return s;
			}
		}
		return null;
	}

	private List<AiKnowledgeSegmentSearchRespBO> recallKnowledgeSegment(String content,
																		AiChatConversationDO conversation) {
		// 1. 查询聊天角色
		if (conversation == null || conversation.getRoleId() == null) {
			return Collections.emptyList();
		}
		AiChatRoleDO role = chatRoleService.getChatRole(conversation.getRoleId());
		if (role == null || CollUtil.isEmpty(role.getKnowledgeIds())) {
			return Collections.emptyList();
		}

		// 2. 遍历找回
		List<AiKnowledgeSegmentSearchRespBO> knowledgeSegments = new ArrayList<>();
		for (Long knowledgeId : role.getKnowledgeIds()) {
			knowledgeSegments.addAll(knowledgeSegmentService.searchKnowledgeSegment(new AiKnowledgeSegmentSearchReqBO()
					.setKnowledgeId(knowledgeId).setContent(content)));
		}
		return knowledgeSegments;
	}

	private Prompt buildPrompt(AiChatConversationDO conversation, List<AiChatMessageDO> messages,
							   List<AiKnowledgeSegmentSearchRespBO> knowledgeSegments,
							   AiModelDO model, AiChatMessageSendReqVO sendReqVO) {
		// 上下文条数限制：携带上下文时，历史问答对已达上限则阻止继续对话
		if (Boolean.TRUE.equals(sendReqVO.getUseContext())) {
			int pairs = countContextPairs(messages);
			if (conversation.getMaxContexts() != null && pairs >= conversation.getMaxContexts()) {
				throw exception(CHAT_PROMPT_TOO_LONG);
			}
		}
		List<Message> chatMessages = new ArrayList<>();
		// 1.1 System Context 角色设定
		if (StrUtil.isNotBlank(conversation.getSystemMessage())) {
			chatMessages.add(new SystemMessage(conversation.getSystemMessage()));
		}

		// 1.2 历史 history message 历史消息
		List<AiChatMessageDO> contextMessages = filterContextMessages(messages, conversation, sendReqVO);
		contextMessages
				.forEach(message -> chatMessages.add(AiUtils.buildMessage(message.getType(), message.getContent())));

		// 1.3 当前 user message 新发送消息
		chatMessages.add(new UserMessage(sendReqVO.getContent()));

		// 1.4 知识库，通过 UserMessage 实现
		if (CollUtil.isNotEmpty(knowledgeSegments)) {
			String reference = knowledgeSegments.stream()
					.map(segment -> "<Reference>" + segment.getContent() + "</Reference>")
					.collect(Collectors.joining("\n\n"));
			chatMessages.add(new UserMessage(String.format(KNOWLEDGE_USER_MESSAGE_TEMPLATE, reference)));
		}

		// 2.1 查询 tool 工具
		Set<String> toolNames = null;
		Map<String, Object> toolContext = Map.of();
		if (conversation.getRoleId() != null) {
			AiChatRoleDO chatRole = chatRoleService.getChatRole(conversation.getRoleId());
			if (chatRole != null && CollUtil.isNotEmpty(chatRole.getToolIds())) {
				toolNames = convertSet(toolService.getToolList(chatRole.getToolIds()), AiToolDO::getName);
				toolContext = AiUtils.buildCommonToolContext();
			}
		}
		// 2.2 构建 ChatOptions 对象
		AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
		String effectiveModel = model.getModel();
		if (sendReqVO.getReasoningEnabled() && (platform == AiPlatformEnum.DEEP_SEEK || (platform == AiPlatformEnum.HUN_YUAN && StrUtil.startWithIgnoreCase(effectiveModel, "deepseek")))) {
			effectiveModel = "deepseek-reasoner";
		}
		ChatOptions chatOptions = AiUtils.buildChatOptions(platform, effectiveModel,
				conversation.getTemperature(), conversation.getMaxTokens(), toolNames, toolContext);
		return new Prompt(chatMessages, chatOptions);
	}

	private int estimateContextTokens(AiChatConversationDO conversation, List<AiChatMessageDO> allMessages, String currentContent) {
		int total = 0;
		AiChatMessageSendReqVO ctxReq = new AiChatMessageSendReqVO();
		ctxReq.setUseContext(true);
		List<AiChatMessageDO> contextMessages = filterContextMessages(allMessages, conversation, ctxReq);
		for (AiChatMessageDO m : contextMessages) {
			total += estimateTokens(m.getContent());
			if (StrUtil.isNotEmpty(m.getReasoningContent())) {
				total += estimateTokens(m.getReasoningContent());
			}
		}
		if (StrUtil.isNotEmpty(currentContent)) {
			total += estimateTokens(currentContent);
		}
		return total;
	}

	private int countContextPairs(List<AiChatMessageDO> messages) {
		int pairs = 0;
		for (int i = messages.size() - 1; i >= 0; i--) {
			AiChatMessageDO assistantMessage = CollUtil.get(messages, i);
			if (assistantMessage == null || assistantMessage.getReplyId() == null) {
				continue;
			}
			AiChatMessageDO userMessage = CollUtil.get(messages, i - 1);
			if (userMessage == null
					|| ObjUtil.notEqual(assistantMessage.getReplyId(), userMessage.getId())
					|| StrUtil.isEmpty(assistantMessage.getContent())) {
				continue;
			}
			pairs++;
			i--; // 跳过已配对的 user
		}
		return pairs;
	}

	private int estimateTokens(String text) {
		if (StrUtil.isEmpty(text)) return 0;
		String ascii = text.replaceAll("[^\\x00-\\x7F]", "");
		int nonAsciiCount = text.length() - ascii.length();
		String[] words = ascii.trim().split("\\s+");
		int asciiTokens = 0;
		for (String w : words) {
			if (StrUtil.isEmpty(w)) continue;
			asciiTokens += (int) Math.ceil(w.length() / 4.0);
		}
		return asciiTokens + nonAsciiCount;
	}

	private String extractReasoningFromText(String text) {
		if (StrUtil.isEmpty(text)) return null;
		StringBuilder sb = new StringBuilder();
		int start = 0;
		while (true) {
			int s = text.indexOf("<thinking>", start);
			if (s < 0) break;
			int e = text.indexOf("</thinking>", s + 10);
			if (e < 0) break;
			sb.append(text, s + 10, e);
			start = e + 11;
		}
		if (sb.length() > 0) return sb.toString();
		start = 0;
		while (true) {
			int s = text.indexOf("<reasoning>", start);
			if (s < 0) break;
			int e = text.indexOf("</reasoning>", s + 11);
			if (e < 0) break;
			sb.append(text, s + 11, e);
			start = e + 12;
		}
		return sb.length() > 0 ? sb.toString() : null;
	}

	private String stripReasoningTags(String text) {
		if (StrUtil.isEmpty(text)) return text;
		String t = text;
		t = t.replaceAll("<thinking>[\\s\\S]*?</thinking>", "");
		t = t.replaceAll("<reasoning>[\\s\\S]*?</reasoning>", "");
		t = t.replaceAll("<answer>", "");
		t = t.replaceAll("</answer>", "");
		return t;
	}

	/**
	 * 从历史消息中，获得倒序的 n 组消息作为消息上下文
	 * <p>
	 * n 组：指的是 user + assistant 形成一组
	 *
	 * @param messages     消息列表
	 * @param conversation 对话
	 * @param sendReqVO    发送请求
	 * @return 消息上下文
	 */
	private List<AiChatMessageDO> filterContextMessages(List<AiChatMessageDO> messages,
														AiChatConversationDO conversation,
														AiChatMessageSendReqVO sendReqVO) {
		if (conversation.getMaxContexts() == null || ObjUtil.notEqual(sendReqVO.getUseContext(), Boolean.TRUE)) {
			return Collections.emptyList();
		}
		List<AiChatMessageDO> contextMessages = new ArrayList<>(conversation.getMaxContexts() * 2);
		for (int i = messages.size() - 1; i >= 0; i--) {
			AiChatMessageDO assistantMessage = CollUtil.get(messages, i);
			if (assistantMessage == null || assistantMessage.getReplyId() == null) {
				continue;
			}
			AiChatMessageDO userMessage = CollUtil.get(messages, i - 1);
			if (userMessage == null
					|| ObjUtil.notEqual(assistantMessage.getReplyId(), userMessage.getId())
					|| StrUtil.isEmpty(assistantMessage.getContent())) {
				continue;
			}
			// 由于后续要 reverse 反转，所以先添加 assistantMessage
			contextMessages.add(assistantMessage);
			contextMessages.add(userMessage);
			// 超过最大上下文，结束
			if (contextMessages.size() >= conversation.getMaxContexts() * 2) {
				break;
			}
		}
		Collections.reverse(contextMessages);
		return contextMessages;
	}

	private AiChatMessageDO createChatMessage(Long conversationId, Long replyId,
											  AiModelDO model, Long userId, Long roleId,
											  MessageType messageType, String content, Boolean useContext,
											  List<AiKnowledgeSegmentSearchRespBO> knowledgeSegments) {
		return createChatMessage(conversationId, replyId, model, userId, roleId, messageType, content, useContext, knowledgeSegments, null);
	}

	private AiChatMessageDO createChatMessage(Long conversationId, Long replyId,
											  AiModelDO model, Long userId, Long roleId,
											  MessageType messageType, String content, Boolean useContext,
											  List<AiKnowledgeSegmentSearchRespBO> knowledgeSegments, Integer msgType) {
		// 如果 msgType 为 null，则根据模型类型自动确定
		Integer actualMsgType = msgType != null ? msgType : determineMessageTypeByModel(model);

		AiChatMessageDO message = new AiChatMessageDO().setConversationId(conversationId).setReplyId(replyId)
				.setModel(model.getModel()).setModelId(model.getId()).setUserId(userId).setRoleId(roleId)
				.setType(messageType.getValue()).setContent(content).setUseContext(useContext)
				.setSegmentIds(convertList(knowledgeSegments, AiKnowledgeSegmentSearchRespBO::getId))
				.setMsgType(actualMsgType);  // 设置消息内容类型（自动确定）
		message.setCreateTime(LocalDateTime.now());
		chatMessageMapper.insert(message);
		return message;
	}

	@Override
	public List<AiChatMessageDO> getChatMessageListByConversationId(Long conversationId) {
		return chatMessageMapper.selectListByConversationId(conversationId);
	}

	@Override
	public void deleteChatMessage(Long id, Long userId) {
		// 1. 校验消息存在
		AiChatMessageDO message = chatMessageMapper.selectById(id);
		if (message == null || ObjUtil.notEqual(message.getUserId(), userId)) {
			throw exception(CHAT_MESSAGE_NOT_EXIST);
		}
		// 2. 执行删除
		chatMessageMapper.deleteById(id);
	}

	@Override
	public void deleteChatMessageByConversationId(AiDelReqVO reqVOS, Long userId) {
		reqVOS.getConversationIdList().forEach(conversationId -> {
			// 1. 校验消息存在
			List<AiChatMessageDO> messages = chatMessageMapper.selectListByConversationId(conversationId);
			if (CollUtil.isEmpty(messages) || ObjUtil.notEqual(messages.get(0).getUserId(), userId)) {
				throw exception(CHAT_MESSAGE_NOT_EXIST);
			}
			// 2. 执行删除
			chatMessageMapper.deleteBatchIds(convertList(messages, AiChatMessageDO::getId));
		});
	}

	@Override
	public void deleteChatMessageByAdmin(Long id) {
		// 1. 校验消息存在
		AiChatMessageDO message = chatMessageMapper.selectById(id);
		if (message == null) {
			throw exception(CHAT_MESSAGE_NOT_EXIST);
		}
		// 2. 执行删除
		chatMessageMapper.deleteById(id);
	}

	@Override
	public Map<Long, Integer> getChatMessageCountMap(Collection<Long> conversationIds) {
		return chatMessageMapper.selectCountMapByConversationId(conversationIds);
	}

	@Override
	public PageResult<AiChatMessageDO> getChatMessagePage(AiChatMessagePageReqVO pageReqVO) {
		return chatMessageMapper.selectPage(pageReqVO);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public AiChatMessageSendRespVO saveGeneratedContent(Long conversationId, String prompt, String generatedContent, Long userId, Integer msgType) {
		// 1. 校验对话存在
		AiChatConversationDO conversation = chatConversationService
				.validateChatConversationExists(conversationId);
		if (ObjUtil.notEqual(conversation.getUserId(), userId)) {
			throw exception(CHAT_CONVERSATION_NOT_EXISTS);
		}

		// 2. 校验模型
		AiModelDO model = modalService.validateModel(conversation.getModelId());

		// 3. 根据模型类型自动确定消息内容类型
		Integer actualMsgType = determineMessageTypeByModel(model);

		// 4. 插入 user 发送消息（用户的提示词）
		AiChatMessageDO userMessage = createChatMessage(conversationId, null, model,
				userId, conversation.getRoleId(), MessageType.USER, prompt, false, null, actualMsgType);

		// 5. 插入 assistant 接收消息（生成的内容）
		AiChatMessageDO assistantMessage = createChatMessage(conversationId, userMessage.getId(), model,
				userId, conversation.getRoleId(), MessageType.ASSISTANT, generatedContent, false, null, actualMsgType);

		// 6. 返回结果
		return new AiChatMessageSendRespVO()
				.setSend(BeanUtils.toBean(userMessage, AiChatMessageSendRespVO.Message.class))
				.setReceive(BeanUtils.toBean(assistantMessage, AiChatMessageSendRespVO.Message.class));
	}

	/**
	 * 根据模型类型确定消息内容类型
	 *
	 * @param model 模型
	 * @return 消息内容类型
	 */
	private Integer determineMessageTypeByModel(AiModelDO model) {
		if (model.getType() == null) {
			return AiChatMessageContentTypeEnum.TEXT.getType();
		}

		// 根据模型类型映射到消息内容类型
		return switch (model.getType()) {
			case 1 -> AiChatMessageContentTypeEnum.TEXT.getType();  // CHAT -> TEXT
			case 2 -> AiChatMessageContentTypeEnum.IMAGE.getType(); // IMAGE -> IMAGE
			case 3 -> AiChatMessageContentTypeEnum.AUDIO.getType(); // AUDIO -> AUDIO
			case 7, 8 -> AiChatMessageContentTypeEnum.VIDEO.getType(); // TEXT_TO_VIDEO/IMAGE_TO_VIDEO -> VIDEO
			default -> AiChatMessageContentTypeEnum.TEXT.getType(); // 默认为文本
		};
	}

}
