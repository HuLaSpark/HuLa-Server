package com.luohuo.flex.ai.service.audio;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.audio.vo.AiAudioGenerateReqVO;
import com.luohuo.flex.ai.controller.audio.vo.AiAudioPageReqVO;
import com.luohuo.flex.ai.core.model.audio.AudioModel;
import com.luohuo.flex.ai.core.model.audio.AudioOptions;
import com.luohuo.flex.ai.core.model.gitee.GiteeAiAudioOptions;
import com.luohuo.flex.ai.core.model.silicon.SiliconFlowAudioOptions;
import com.luohuo.flex.ai.dal.audio.AiAudioDO;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.enums.AiImageStatusEnum;
import com.luohuo.flex.ai.enums.AiPlatformEnum;
import com.luohuo.flex.ai.mapper.audio.AiAudioMapper;
import com.luohuo.flex.ai.service.chat.AiChatMessageService;
import com.luohuo.flex.ai.service.model.AiModelService;
import com.luohuo.flex.ai.service.model.AiModelUsageService;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.flex.service.SysConfigService;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.luohuo.flex.ai.enums.ErrorCodeConstants.AUDIO_NOT_EXISTS;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;

@Service
@Slf4j
public class AiAudioServiceImpl implements AiAudioService {

    @Resource
    private AiAudioMapper audioMapper;

    @Resource
    private AiModelService modelService;

    @Resource
    private AiModelUsageService modelUsageService;

    @Resource
    private AiChatMessageService chatMessageService;

    @Resource
    private SysConfigService sysConfigService;

    @Override
    public PageResult<AiAudioDO> getAudioPageMy(Long userId, AiAudioPageReqVO pageReqVO) {
        pageReqVO.setUserId(userId);
        return audioMapper.selectPage(pageReqVO);
    }

    @Override
    public AiAudioDO getAudio(Long id) {
        return audioMapper.selectById(id);
    }

    @Override
    public List<AiAudioDO> getAudioList(List<Long> ids) {
        return audioMapper.selectListByIds(ids);
    }

    @Override
    public Long generateAudio(Long userId, AiAudioGenerateReqVO generateReqVO) {
        // 1. 校验模型
        AiModelDO model = modelService.validateModel(generateReqVO.getModelId());

        // 2. 检查并扣减模型使用次数
        modelUsageService.checkAndDeductUsage(userId, model);

        // 3. 保存数据库
        AiAudioDO audio = BeanUtils.toBean(generateReqVO, AiAudioDO.class).setUserId(userId)
                .setPlatform(model.getPlatform()).setModelId(model.getId()).setModel(model.getModel())
                .setPublicStatus(false).setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        audioMapper.insert(audio);

        // 4. 异步生成音频
        executeGenerateAudio(audio, generateReqVO, model);
        return audio.getId();
    }

    @Async
    public void executeGenerateAudio(AiAudioDO audio, AiAudioGenerateReqVO reqVO, AiModelDO model) {
        try {
            String audioUrl;

            // 使用 AudioModel 接口统一处理音频生成
            AudioModel audioModel = modelService.getAudioModel(model.getId());

            // 构建音频生成参数
            AudioOptions audioOptions = buildAudioOptions(reqVO, model);

            // 生成音频
            byte[] audioData = audioModel.generateSpeech(reqVO.getPrompt(), audioOptions);
            audioUrl = uploadAudioToStorage(audioData);

            audioMapper.updateById(new AiAudioDO().setId(audio.getId())
                    .setAudioUrl(audioUrl)
                    .setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                    .setFinishTime(LocalDateTime.now()));

            if (audio.getConversationId() != null) {
                createChatMessage(audio, audioUrl, reqVO);
            }

        } catch (Exception e) {
            log.error("[executeGenerateAudio][audio({}) 生成失败]", audio.getId(), e);
            audioMapper.updateById(new AiAudioDO().setId(audio.getId())
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(e.getMessage())
                    .setFinishTime(LocalDateTime.now()));
        }
    }

    /**
     * 构建音频生成参数
     */
    private static AudioOptions buildAudioOptions(AiAudioGenerateReqVO reqVO, AiModelDO model) {
        // 从 options 中提取自定义参数
        String voice = null;
        Double speed = null;
        String responseFormat = null;

        if (reqVO.getOptions() != null) {
            voice = reqVO.getOptions().get("voice");
            String speedStr = reqVO.getOptions().get("speed");
            if (speedStr != null) {
                try {
                    speed = Double.parseDouble(speedStr);
                } catch (NumberFormatException e) {
                    // 使用默认值
                    speed = 1.0;
                }
            }
            responseFormat = reqVO.getOptions().get("responseFormat");
        }

        if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.SILICON_FLOW.getPlatform())) {
            return SiliconFlowAudioOptions.builder()
                    .model(model.getModel())
                    .voice(voice)
                    .speed(speed != null ? speed : 1.0)
                    .responseFormat(responseFormat != null ? responseFormat : "mp3")
                    .build();
        } else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.GITEE_AI.getPlatform())) {
            return GiteeAiAudioOptions.builder()
                    .model(model.getModel())
                    .speed(speed != null ? speed : 1.0)
                    .responseFormat(responseFormat != null ? responseFormat : "blob")  // Gitee AI 只支持 blob 或 url
                    .build();
        }

        // TODO: 其他平台的 AudioOptions 构建逻辑
        // else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.DEEP_SEEK.getPlatform())) {
        //     return DeepSeekAudioOptions.builder()...
        // }

        throw new IllegalArgumentException("不支持的平台：" + model.getPlatform());
    }

    /**
     * 创建聊天消息
     */
    private void createChatMessage(AiAudioDO audio, String audioUrl, AiAudioGenerateReqVO reqVO) {
        try {
            // 直接存储音频 URL，不使用 Markdown 格式
            var result = chatMessageService.saveGeneratedContent(
                    audio.getConversationId(),
                    reqVO.getPrompt(),
                    audioUrl,  // 直接存储 URL
                    audio.getUserId(),
                    null
            );

            // 更新 audio 记录的 chatMessageId
            audioMapper.updateById(new AiAudioDO().setId(audio.getId())
                    .setChatMessageId(result.getReceive().getId()));

            log.info("[createChatMessage] 音频生成成功并创建聊天消息，audioId={}, chatMessageId={}",
                    audio.getId(), result.getReceive().getId());
        } catch (Exception e) {
            log.error("[createChatMessage] 创建聊天消息失败，audioId={}", audio.getId(), e);
        }
    }

    private String uploadAudioToStorage(byte[] audioData) {
        try {
            String accessKey = sysConfigService.get("qnAccessKey");
            String secretKey = sysConfigService.get("qnSecretKey");
            String bucket = sysConfigService.get("qnStorageName");
            String cdnDomain = sysConfigService.get("qnStorageCDN");

            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            Configuration cfg = new Configuration(Region.autoRegion());
            UploadManager uploadManager = new UploadManager(cfg);

            String fileName = "ai/audio/" + IdUtil.fastSimpleUUID() + ".mp3";

            // 上传到七牛云
            Response response = uploadManager.put(audioData, fileName, upToken);

            if (response.isOK()) {
                String url = cdnDomain;
                if (!url.endsWith("/")) {
                    url += "/";
                }
                url += fileName;
                return url;
            } else {
                throw new RuntimeException(response.bodyString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAudioMy(Long id, Long userId) {
        AiAudioDO audio = audioMapper.selectById(id);
        if (audio == null) {
            throw exception(AUDIO_NOT_EXISTS);
        }
        if (ObjUtil.notEqual(audio.getUserId(), userId)) {
            throw exception(AUDIO_NOT_EXISTS);
        }
        audioMapper.deleteById(id);
    }
}
