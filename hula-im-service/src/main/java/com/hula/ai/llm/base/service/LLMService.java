package com.hula.ai.llm.base.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.client.enums.ChatRoleEnum;
import com.hula.ai.client.enums.ChatStatusEnum;
import com.hula.ai.client.model.command.ChatCommand;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.model.command.CompletionsParam;
import com.hula.ai.client.model.command.DeleteFileParam;
import com.hula.ai.client.model.command.UploadParam;
import com.hula.ai.client.model.dto.ChatMessageDTO;
import com.hula.ai.client.service.GptService;
import com.hula.ai.common.enums.IntegerEnum;
import com.hula.ai.common.utils.DozerUtil;
import com.hula.ai.framework.util.file.FileUploadResponse;
import com.hula.ai.llm.base.entity.ChatData;
import com.hula.ai.llm.base.exception.LLMException;
import com.hula.ai.llm.base.service.impl.*;
import com.hula.ai.llm.chatglm.ChatGLMClient;
import com.hula.ai.llm.deepseek.DeepSeekStreamClient;
import com.hula.ai.llm.doubao.DouBaoClient;
import com.hula.ai.llm.internlm.InternlmClient;
import com.hula.ai.llm.locallm.coze.CozeClient;
import com.hula.ai.llm.locallm.gitee.GiteeClient;
import com.hula.ai.llm.locallm.langchain.LangchainClient;
import com.hula.ai.llm.locallm.ollama.OllamaClient;
import com.hula.ai.llm.moonshot.MoonshotClient;
import com.hula.ai.llm.openai.OpenAiClient;
import com.hula.ai.llm.openai.OpenAiStreamClient;
import com.hula.ai.llm.spark.SparkClient;
import com.hula.ai.llm.tongyi.TongYiClient;
import com.hula.ai.llm.wenxin.WenXinClient;
import com.hula.domain.vo.res.ApiResult;
import com.hula.exception.BizException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

/**
 * 大模型 接口实现
 *
 * @author: 云裂痕
 * @date: 2025/03/07
 * @version: 1.2.8
 * 得其道 乾乾
 */
@Slf4j
@Service
public class LLMService {
    private static final String[] drawingWords = {"画画", "作画", "画图", "绘画", "描绘"};
    private static final String[] drawingInstructions = {"请画", "画一", "画个",};
    private static OpenAiClient openAiClient;
    private static OpenAiStreamClient openAiStreamClient;
    private static WenXinClient wenXinClient;
    private static ChatGLMClient chatGLMClient;
    private static TongYiClient tongYiClient;
    private static SparkClient sparkClient;
    private static MoonshotClient moonshotClient;
    private static DeepSeekStreamClient deepSeekStreamClient;
    private static DouBaoClient douBaoClient;
    private static InternlmClient internlmClient;
    private static LangchainClient langchainClient;
    private static OllamaClient ollamaClient;
    private static CozeClient cozeClient;
    private final GptService gptService;
    private static GiteeClient giteeClient;

    @Autowired
    public LLMService(GptService gptService, OpenAiClient openAiClient, OpenAiStreamClient openAiStreamClient, WenXinClient wenXinClient,
					  ChatGLMClient chatGLMClient, TongYiClient tongYiClient, SparkClient sparkClient, MoonshotClient moonshotClient, DeepSeekStreamClient deepSeekStreamClient,
					  DouBaoClient douBaoClient, InternlmClient internlmClient, LangchainClient langchainClient, OllamaClient ollamaClient, CozeClient cozeClient, GiteeClient giteeClient) {
        this.gptService = gptService;
        LLMService.openAiClient = openAiClient;
        LLMService.openAiStreamClient = openAiStreamClient;
        LLMService.wenXinClient = wenXinClient;
        LLMService.chatGLMClient = chatGLMClient;
        LLMService.tongYiClient = tongYiClient;
        LLMService.sparkClient = sparkClient;
        LLMService.moonshotClient = moonshotClient;
        LLMService.deepSeekStreamClient = deepSeekStreamClient;
        LLMService.douBaoClient = douBaoClient;
        LLMService.internlmClient = internlmClient;
        LLMService.langchainClient = langchainClient;
        LLMService.ollamaClient = ollamaClient;
        LLMService.cozeClient = cozeClient;
        LLMService.giteeClient = giteeClient;
    }

    public SseEmitter createSse(Long uid) {
        //默认30秒超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(0L);
        //完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接", uid);
            LocalCache.CACHE.remove(uid);
        });
        //超时回调
        sseEmitter.onTimeout(() -> {
            log.info("[{}]连接超时", uid);
        });
        //异常回调
        sseEmitter.onError(
			throwable -> {
				try {
					log.info("[{}]连接异常,{}", uid, throwable.toString());
					sseEmitter.send(SseEmitter.event()
							.id(uid.toString())
							.name("发生异常！")
							.data(ChatData.builder().content("发生异常请重试！").build())
							.reconnectTime(3000));
					LocalCache.CACHE.put(uid, sseEmitter);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        );
        try {
            sseEmitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            e.printStackTrace();
        }
        LocalCache.CACHE.put(uid, sseEmitter);
        log.info("[{}]创建sse连接成功！", uid);
        return sseEmitter;
    }

    public void closeSse(Long uid) {
        SseEmitter sse = (SseEmitter) LocalCache.CACHE.get(uid);
        if (sse != null) {
            sse.complete();
            //移除
            LocalCache.CACHE.remove(uid);
        }
    }

	public Boolean deleteFile(DeleteFileParam param) {
		return getLLM(ChatModelEnum.getEnum(param.getModel())).deleteFile(param.getFileIds());
	}

	public JSONArray fileList(String model) {
		return getLLM(ChatModelEnum.getEnum(model)).fileList();
	}

	public FileUploadResponse uploadFile(MultipartFile file, UploadParam param) {
		return getLLM(ChatModelEnum.getEnum(param.getModel())).uploadFile(file);
	}

    /**
     * 根据模型获取大模型实现
     *
     * @param model
     * @return
     */
    private ModelService getLLM(ChatModelEnum model) {
        switch (model) {
            case OPENAI -> {
				return new OpenAIServiceImpl(openAiClient, openAiStreamClient);
			}
            case WENXIN -> {
				return new WenXinServiceImpl(gptService, wenXinClient);
			}
            case TONGYI -> {
				return new TongYiServiceImpl(tongYiClient);
			}
            case SPARK -> {
				return new SparkServiceImpl(sparkClient);
			}
            case CHATGLM -> {
				return new ChatGLMServiceImpl(chatGLMClient);
			}
            case INTERNLM -> {
				return new InternLMServiceImpl(internlmClient);
			}
            case MOONSHOT -> {
				return new MoonshotServiceImpl(moonshotClient);
			}
            case DEEPSEEK -> {
				return new DeepSeekServiceImpl(deepSeekStreamClient);
			}
            case DOUBAO -> {
				return new DouBaoServiceImpl(douBaoClient);
			}
            case LOCALLM -> {
				return new LocalLMServiceImpl(langchainClient, ollamaClient, cozeClient, gptService,giteeClient);
			}
			default -> {
				return null;
			}
		}
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiResult chat(ChatCommand command) {
        command = gptService.validateGptCommand(command);
        ChatMessageDTO userMessage = new ChatMessageDTO();
        ChatMessageCommand chatMessage;
        try {
            Long chatId = gptService.saveChat(command);
            List<ChatMessageDTO> messages = gptService.saveChatMessage(command, chatId, command.getConversationId());
            userMessage = messages.get(messages.size() - 1);
            if (CollUtil.isEmpty(messages)) {
                throw new BizException("消息发送失败");
            }
            ChatModelEnum modelEnum = ChatModelEnum.getEnum(command.getModel());
            String version = command.getModelVersion();
            chatMessage = getLLM(modelEnum).chat(messages, isDraw(command.getPrompt()), chatId, version);
        } catch (LLMException e) {
            gptService.updateMessageStatus(userMessage.getMessageId(), IntegerEnum.THREE.getValue());
            return ApiResult.success();
        }
        chatMessage.setParentMessageId(userMessage.getMessageId());
        gptService.saveChatMessage(chatMessage);
        gptService.updateMessageStatus(userMessage.getMessageId(), IntegerEnum.TWO.getValue());
        return ApiResult.success(DozerUtil.convertor(chatMessage, ChatMessageDTO.class));
    }

    public void sseChat(HttpServletResponse response, Boolean isWs, Long uid, CompletionsParam completionsParam) {
        ChatMessageDTO chatMessage = gptService.getMessageByConverstationId(completionsParam.getConversationId());
        String prompt = chatMessage.getContent();
        String version = chatMessage.getModelVersion();
        ChatModelEnum modelEnum = ChatModelEnum.getEnum(chatMessage.getModel());
        SseEmitter sseEmitter = createSse(uid);
        if (sseEmitter == null) {
            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
            throw new BizException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
        }
        List<ChatMessageDTO> chatMessages = gptService.listMessageByConverstationId(uid, completionsParam.getConversationId());
        if (CollUtil.isEmpty(chatMessages)) {
            throw new BizException("消息发送失败");
        }
        Boolean error = false;
        Integer status = ChatStatusEnum.SUCCESS.getValue();
        try {
			ModelService modelService = getLLM(modelEnum);
			if(CollUtil.isNotEmpty(completionsParam.getFileIds())){
				for (String fileId : completionsParam.getFileIds()) {
					String content = modelService.fileContent(fileId);
					if(StrUtil.isNotEmpty(content)){
						ChatMessageDTO messageDTO = new ChatMessageDTO();
						messageDTO.setContent(content);
						messageDTO.setRole(ChatRoleEnum.USER.getValue());
						chatMessages.add(0, messageDTO);
					}
				}
			}

            // ChatGPT、文心一言统一在SSEListener中处理流式返回，通义千问与讯飞星火\智谱清言单独处理
            error = modelService.streamChat(response, sseEmitter, chatMessages, isWs, isDraw(prompt), chatMessage.getChatId(), completionsParam.getConversationId(), prompt, version, uid);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException();
        } finally {
            gptService.updateMessageStatus(completionsParam.getConversationId(), error ? ChatStatusEnum.ERROR.getValue() : status);
            if (error) {
                throw new BizException("模型输出失败");
            }
        }
    }

    /**
     * 判断是否需要画画
     *
     * @param prompt 输入内容
     * @return
     */
    private Boolean isDraw(String prompt) {
        // 检查是否有明确的画画词汇
        for (String word : drawingWords) {
            if (prompt.contains(word)) {
                return true;
            }
        }
        // 检查是否有指导性的画画语句
        for (String instruction : drawingInstructions) {
            if (prompt.contains(instruction)) {
                return true;
            }
        }
        // 检查是否有描述画画的动作的词或其他相关名词
        // 这里只是一个示例，实际上需要更复杂的词性标注和语境分析
        if (prompt.contains("画") || prompt.contains("绘画")) {
            return true;
        }
        return false;
    }

}
