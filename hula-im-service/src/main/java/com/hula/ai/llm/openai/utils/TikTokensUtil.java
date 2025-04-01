package com.hula.ai.llm.openai.utils;

import cn.hutool.core.util.StrUtil;
import com.hula.ai.llm.openai.entity.chat.ChatCompletion;
import com.hula.ai.llm.openai.entity.chat.FunctionCall;
import com.hula.ai.llm.openai.entity.chat.Message;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.ModelType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：token计算工具类
 */
@Slf4j
public class TikTokensUtil {
    /**
     * 模型名称对应Encoding
     */
    private static final Map<String, Encoding> modelMap = new HashMap<>();
    /**
     * registry实例
     */
    private static final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();

    static {
        for (ModelType modelType : ModelType.values()) {
            modelMap.put(modelType.getName(), registry.getEncodingForModel(modelType));
        }
        modelMap.put(ChatCompletion.Model.GPT_3_5_TURBO_0301.getName(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ChatCompletion.Model.GPT_3_5_TURBO_0613.getName(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ChatCompletion.Model.GPT_3_5_TURBO_16K.getName(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ChatCompletion.Model.GPT_3_5_TURBO_16K_0613.getName(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ChatCompletion.Model.GPT_3_5_TURBO_1106.getName(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ChatCompletion.Model.GPT_4_32K.getName(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ChatCompletion.Model.GPT_4_32K_0314.getName(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ChatCompletion.Model.GPT_4_0314.getName(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ChatCompletion.Model.GPT_4_0613.getName(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ChatCompletion.Model.GPT_4_32K_0613.getName(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ChatCompletion.Model.GPT_4_1106_PREVIEW.getName(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ChatCompletion.Model.GPT_4_VISION_PREVIEW.getName(), registry.getEncodingForModel(ModelType.GPT_4));
    }

    /**
     * 通过Encoding和text获取编码数组
     *
     * @param enc  Encoding类型
     * @param text 文本信息
     * @return 编码数组
     */
    public static List<Integer> encode(@NotNull Encoding enc, String text) {
        return StrUtil.isBlank(text) ? new ArrayList<>() : enc.encode(text);
    }

    /**
     * 通过Encoding计算text信息的tokens
     *
     * @param enc  Encoding类型
     * @param text 文本信息
     * @return tokens数量
     */
    public static int tokens(@NotNull Encoding enc, String text) {
        return encode(enc, text).size();
    }


    /**
     * 获取一个Encoding对象，通过Encoding类型
     *
     * @param encodingType encodingType
     * @return Encoding
     */
    public static Encoding getEncoding(@NotNull EncodingType encodingType) {
        return registry.getEncoding(encodingType);
    }

    /**
     * 获取encode的编码数组
     *
     * @param text 文本信息
     * @return 编码数组
     */
    public static List<Integer> encode(@NotNull EncodingType encodingType, String text) {
        if (StrUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        Encoding enc = getEncoding(encodingType);
        return enc.encode(text);
    }

    /**
     * 计算指定字符串的tokens，通过EncodingType
     *
     * @param encodingType encodingType
     * @param text         文本信息
     * @return tokens数量
     */
    public static int tokens(@NotNull EncodingType encodingType, String text) {
        return encode(encodingType, text).size();
    }

    /**
     * 获取一个Encoding对象，通过模型名称
     *
     * @param modelName 模型名称
     * @return Encoding
     */
    public static Encoding getEncoding(@NotNull String modelName) {
        return modelMap.get(modelName);
    }

    /**
     * 获取encode的编码数组，通过模型名称
     *
     * @param text 文本信息
     * @return 编码数组
     */
    public static List<Integer> encode(@NotNull String modelName, String text) {
        if (StrUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        Encoding enc = getEncoding(modelName);
        if (Objects.isNull(enc)) {
            log.warn("[{}]模型不存在或者暂不支持计算tokens，直接返回tokens==0");
            return new ArrayList<>();
        }
        return enc.encode(text);
    }

    /**
     * 通过模型名称, 计算指定字符串的tokens
     *
     * @param modelName 模型名称
     * @param text      文本信息
     * @return tokens数量
     */
    public static int tokens(@NotNull String modelName, String text) {
        return encode(modelName, text).size();
    }


    /**
     * 通过模型名称计算messages获取编码数组
     * 参考官方的处理逻辑：
     * <a href=https://github.com/openai/openai-cookbook/blob/main/examples/How_to_count_tokens_with_tiktoken.ipynb>https://github.com/openai/openai-cookbook/blob/main/examples/How_to_count_tokens_with_tiktoken.ipynb</a>
     *
     * @param modelName 模型名称
     * @param messages  消息体
     * @return tokens数量
     */
    public static int tokens(@NotNull String modelName, @NotNull List<Message> messages) {
        Encoding encoding = getEncoding(modelName);
        int tokensPerMessage = 0;
        int tokensPerName = 0;
        if (modelName.equals(ChatCompletion.Model.GPT_3_5_TURBO_0613.getName())
                || modelName.equals(ChatCompletion.Model.GPT_3_5_TURBO_16K_0613.getName())
                || modelName.equals(ChatCompletion.Model.GPT_4_0314.getName())
                || modelName.equals(ChatCompletion.Model.GPT_4_32K_0314.getName())
                || modelName.equals(ChatCompletion.Model.GPT_4_0613.getName())
                || modelName.equals(ChatCompletion.Model.GPT_4_32K_0613.getName())
        ) {
            tokensPerMessage = 3;
            tokensPerName = 1;
        } else if (modelName.equals(ChatCompletion.Model.GPT_3_5_TURBO_0301.getName())) {
            tokensPerMessage = 4;
            tokensPerName = -1;
        } else if (modelName.contains(ChatCompletion.Model.GPT_3_5_TURBO.getName())) {
            //"gpt-3.5-turbo" in model:
            log.warn("Warning: gpt-3.5-turbo may update over time. Returning num tokens assuming gpt-3.5-turbo-0613.");
            tokensPerMessage = 3;
            tokensPerName = 1;
        } else if (modelName.contains(ChatCompletion.Model.GPT_4.getName())) {
            log.warn("Warning: gpt-4 may update over time. Returning num tokens assuming gpt-4-0613.");
            tokensPerMessage = 3;
            tokensPerName = 1;
        } else {
            log.warn("不支持的model {}. See https://github.com/openai/openai-python/blob/main/chatml.md 更多信息.", modelName);
        }
        int sum = 0;
        for (Message msg : messages) {
            sum += tokensPerMessage;
            sum += tokens(encoding, msg.getContent());
            sum += tokens(encoding, msg.getRole());
            sum += tokens(encoding, msg.getName());
            FunctionCall functionCall = msg.getFunctionCall();
            sum += Objects.isNull(functionCall) ? 0 : tokens(encoding, functionCall.toString());
            if (StrUtil.isNotBlank(msg.getName())) {
                sum += tokensPerName;
            }
        }
        sum += 3;
        return sum;
    }

    /**
     * 获取modelType
     *
     * @param name 模型名称
     * @return ModelType
     */
    public static ModelType getModelTypeByName(String name) {
        if (
                ChatCompletion.Model.GPT_3_5_TURBO_0301.getName().equals(name) ||
                        ChatCompletion.Model.GPT_3_5_TURBO_0613.getName().equals(name) ||
                        ChatCompletion.Model.GPT_3_5_TURBO_16K.getName().equals(name) ||
                        ChatCompletion.Model.GPT_3_5_TURBO_16K_0613.getName().equals(name)
        ) {
            return ModelType.GPT_3_5_TURBO;
        }
        if (ChatCompletion.Model.GPT_4.getName().equals(name)
                || ChatCompletion.Model.GPT_4_32K.getName().equals(name)
                || ChatCompletion.Model.GPT_4_32K_0314.getName().equals(name)
                || ChatCompletion.Model.GPT_4_0314.getName().equals(name)
                || ChatCompletion.Model.GPT_4_0613.getName().equals(name)
                || ChatCompletion.Model.GPT_4_32K_0613.getName().equals(name)
        ) {
            return ModelType.GPT_4;
        }

        for (ModelType modelType : ModelType.values()) {
            if (modelType.getName().equals(name)) {
                return modelType;
            }
        }
        log.warn("[{}]模型不存在或者暂不支持计算tokens", name);
        return null;
    }
}
