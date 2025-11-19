package com.luohuo.flex.ai.core.model.gitee;

import com.luohuo.flex.ai.core.model.audio.AudioModel;
import com.luohuo.flex.ai.core.model.audio.AudioOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Set;

/**
 * Gitee AI 音频生成模型实现类
 *
 * <p>实现 {@link AudioModel} 接口，封装 Gitee AI 平台的音频生成功能。
 * 注意：Gitee AI 的音频接口和 OpenAI 不完全兼容，不支持 voice 参数。</p>
 *
 * @author 乾乾
 * @see AudioModel
 * @see GiteeAiAudioOptions
 */
@Slf4j
public class GiteeAiAudioModel implements AudioModel {

    /**
     * Gitee AI Base URL
     */
    public static final String BASE_URL = "https://ai.gitee.com/v1";

    /**
     * Gitee AI 音频 API 客户端
     */
    private final GiteeAiAudioApi giteeAiAudioApi;

    /**
     * 默认配置参数
     */
    private final GiteeAiAudioOptions defaultOptions;

    /**
     * 构造函数 - 使用默认配置
     *
     * @param giteeAiAudioApi Gitee AI 音频 API 客户端
     */
    public GiteeAiAudioModel(GiteeAiAudioApi giteeAiAudioApi) {
        this(giteeAiAudioApi, GiteeAiAudioOptions.builder()
                .speed(1.0)
                .responseFormat("blob")
                .build());
    }

    /**
     * 构造函数 - 自定义默认配置
     *
     * @param giteeAiAudioApi Gitee AI 音频 API 客户端
     * @param defaultOptions 默认配置
     */
    public GiteeAiAudioModel(GiteeAiAudioApi giteeAiAudioApi, GiteeAiAudioOptions defaultOptions) {
        this.giteeAiAudioApi = giteeAiAudioApi;
        this.defaultOptions = defaultOptions;
    }

    /**
     * 生成语音
     *
     * @param prompt 文本内容
     * @param options 音频生成参数
     * @return 音频数据（字节数组）
     */
    @Override
    public byte[] generateSpeech(String prompt, AudioOptions options) {
        Assert.hasText(prompt, "Prompt cannot be empty");

        // 转换为 Gitee AI 的参数格式
        GiteeAiAudioOptions giteeOptions = convertToGiteeAiOptions(options);

        // 合并默认参数和运行时参数
        GiteeAiAudioOptions mergedOptions = mergeOptions(giteeOptions);

        // 构建 Gitee AI 请求参数
        GiteeAiAudioApi.AudioRequest request = new GiteeAiAudioApi.AudioRequest(
                mergedOptions.getModel(),
                prompt,
                mergedOptions.getResponseFormat() != null ? mergedOptions.getResponseFormat() : "blob"
        );

        // 调用 Gitee AI API 生成音频
        return giteeAiAudioApi.createSpeech(request);
    }

    /**
     * 获取支持的音色列表
     *
     * <p>注意：Gitee AI 不支持 voice 参数，返回空集合</p>
     *
     * @return 空集合
     */
    @Override
    public Set<String> getSupportedVoices() {
        return Collections.emptySet();
    }

    /**
     * 转换为 Gitee AI 参数格式
     */
    private GiteeAiAudioOptions convertToGiteeAiOptions(AudioOptions options) {
        if (options instanceof GiteeAiAudioOptions) {
            return (GiteeAiAudioOptions) options;
        }

        if (options == null) {
            return GiteeAiAudioOptions.builder().build();
        }

        // 从通用接口转换
        return GiteeAiAudioOptions.builder()
                .model(options.getModel())
                .voice(options.getVoice())
                .speed(options.getSpeed())
                .responseFormat(options.getResponseFormat())
                .build();
    }

    /**
     * 合并默认参数和运行时参数
     */
    private GiteeAiAudioOptions mergeOptions(GiteeAiAudioOptions runtimeOptions) {
        if (runtimeOptions == null) {
            return defaultOptions;
        }

        return GiteeAiAudioOptions.builder()
                .model(runtimeOptions.getModel() != null ? runtimeOptions.getModel() : defaultOptions.getModel())
                .speed(runtimeOptions.getSpeed() != null ? runtimeOptions.getSpeed() : defaultOptions.getSpeed())
                .responseFormat(runtimeOptions.getResponseFormat() != null ?
                        runtimeOptions.getResponseFormat() : defaultOptions.getResponseFormat())
                .build();
    }
}
