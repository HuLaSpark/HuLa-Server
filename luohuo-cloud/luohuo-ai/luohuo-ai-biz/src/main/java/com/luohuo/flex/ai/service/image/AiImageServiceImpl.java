package com.luohuo.flex.ai.service.image;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.image.vo.AiImageDrawReqVO;
import com.luohuo.flex.ai.controller.image.vo.AiImagePageReqVO;
import com.luohuo.flex.ai.controller.image.vo.AiImagePublicPageReqVO;
import com.luohuo.flex.ai.controller.image.vo.AiImageUpdateReqVO;
import com.luohuo.flex.ai.controller.image.vo.midjourney.AiMidjourneyActionReqVO;
import com.luohuo.flex.ai.controller.image.vo.midjourney.AiMidjourneyImagineReqVO;
import com.luohuo.flex.ai.core.model.MidjourneyApi;
import com.luohuo.flex.ai.core.model.silicon.SiliconFlowImageOptions;
import com.luohuo.flex.ai.dal.image.AiImageDO;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.enums.AiImageStatusEnum;
import com.luohuo.flex.ai.enums.AiPlatformEnum;
import com.luohuo.flex.ai.mapper.image.AiImageMapper;
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
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.qianfan.QianFanImageOptions;
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
import org.springframework.ai.zhipuai.ZhiPuAiImageOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.luohuo.basic.utils.collection.CollectionUtils.convertMap;
import static com.luohuo.basic.utils.collection.CollectionUtils.convertSet;
import static com.luohuo.flex.ai.enums.ErrorCodeConstants.*;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;

/**
 * AI 绘画 Service 实现类
 *
 * @author 乾乾
 */
@Service
@Slf4j
public class AiImageServiceImpl implements AiImageService {

    @Resource
    private AiModelService modelService;

    @Resource
    private AiImageMapper imageMapper;

    @Resource
    private AiModelUsageService modelUsageService;

    @Resource
    private AiChatMessageService chatMessageService;

    @Resource
    private SysConfigService sysConfigService;

    @Override
    public PageResult<AiImageDO> getImagePageMy(Long userId, AiImagePageReqVO pageReqVO) {
        return imageMapper.selectPageMy(userId, pageReqVO);
    }

    @Override
    public PageResult<AiImageDO> getImagePagePublic(AiImagePublicPageReqVO pageReqVO) {
        return imageMapper.selectPage(pageReqVO);
    }

    @Override
    public AiImageDO getImage(Long id) {
        return imageMapper.selectById(id);
    }

    @Override
    public List<AiImageDO> getImageList(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return imageMapper.selectBatchIds(ids);
    }

    @Override
    public Long drawImage(Long userId, AiImageDrawReqVO drawReqVO) {
        // 1. 校验模型
        AiModelDO model = modelService.validateModel(drawReqVO.getModelId());

        // 2. 检查并扣减模型使用次数
        modelUsageService.checkAndDeductUsage(userId, model);

        // 3. 保存数据库
        AiImageDO image = BeanUtils.toBean(drawReqVO, AiImageDO.class).setUserId(userId)
                .setPlatform(model.getPlatform()).setModelId(model.getId()).setModel(model.getModel())
                .setPublicStatus(false).setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus());
        imageMapper.insert(image);

        // 4. 异步绘制，后续前端通过返回的 id 进行轮询结果
        getSelf().executeDrawImage(image, drawReqVO, model);
        return image.getId();
    }

    @Async
    public void executeDrawImage(AiImageDO image, AiImageDrawReqVO reqVO, AiModelDO model) {
        try {
            // 1.1 构建请求
            ImageOptions request = buildImageOptions(reqVO, model);
            // 1.2 执行请求
            ImageModel imageModel = modelService.getImageModel(model.getId());
            ImageResponse response = imageModel.call(new ImagePrompt(reqVO.getPrompt(), request));
            if (response.getResult() == null) {
                throw new IllegalArgumentException("生成结果为空");
            }

            // 1.3 提取图片 URL
            String picUrl = extractImageUrl(response, model);

            // 2. 更新数据库
            imageMapper.updateById(new AiImageDO().setId(image.getId()).setStatus(AiImageStatusEnum.SUCCESS.getStatus())
                    .setPicUrl(picUrl).setFinishTime(LocalDateTime.now()));

            // 3. 创建聊天消息(异步执行,不影响主流程)
            try {
                createChatMessage(image, picUrl);
            } catch (Exception e) {
                log.error("[executeDrawImage] 创建聊天消息失败，imageId={}", image.getId(), e);
            }
        } catch (Exception ex) {
            log.error("[executeDrawImage][image({}) 生成异常]", image, ex);
            imageMapper.updateById(new AiImageDO().setId(image.getId())
                    .setStatus(AiImageStatusEnum.FAIL.getStatus())
                    .setErrorMessage(ex.getMessage()).setFinishTime(LocalDateTime.now()));
        }
    }

    private static ImageOptions buildImageOptions(AiImageDrawReqVO draw, AiModelDO model) {
        if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.OPENAI.getPlatform())) {
            // https://platform.openai.com/docs/api-reference/images/create
            return OpenAiImageOptions.builder().withModel(model.getModel())
                    .withHeight(draw.getHeight()).withWidth(draw.getWidth())
                    .withStyle(MapUtil.getStr(draw.getOptions(), "style")) // 风格
                    .withResponseFormat("b64_json")
                    .build();
        } else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.GITEE_AI.getPlatform())) {
            // https://ai.gitee.com/docs/products/apis/images-vision/lora
            // Gitee AI 兼容 OpenAI 图片生成接口
            return OpenAiImageOptions.builder().withModel(model.getModel())
                    .withHeight(draw.getHeight()).withWidth(draw.getWidth())
                    .withStyle(MapUtil.getStr(draw.getOptions(), "style")) // 风格
                    .withResponseFormat("b64_json")
                    .build();
        } else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.SILICON_FLOW.getPlatform())) {
            // https://docs.siliconflow.cn/cn/api-reference/images/images-generations
            return SiliconFlowImageOptions.builder().model(model.getModel())
                    .height(draw.getHeight()).width(draw.getWidth())
                    .build();
        }  else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.STABLE_DIFFUSION.getPlatform())) {
            // https://platform.stability.ai/docs/api-reference#tag/SDXL-and-SD1.6/operation/textToImage
            // https://platform.stability.ai/docs/api-reference#tag/Text-to-Image/operation/textToImage
            return StabilityAiImageOptions.builder().model(model.getModel())
                    .height(draw.getHeight()).width(draw.getWidth())
                    .seed(Long.valueOf(draw.getOptions().get("seed")))
                    .cfgScale(Float.valueOf(draw.getOptions().get("scale")))
                    .steps(Integer.valueOf(draw.getOptions().get("steps")))
                    .sampler(String.valueOf(draw.getOptions().get("sampler")))
                    .stylePreset(String.valueOf(draw.getOptions().get("stylePreset")))
                    .clipGuidancePreset(String.valueOf(draw.getOptions().get("clipGuidancePreset")))
                    .build();
        } else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.TONG_YI.getPlatform())) {
            return DashScopeImageOptions.builder()
                    .withModel(model.getModel()).withN(1)
                    .withHeight(draw.getHeight()).withWidth(draw.getWidth())
                    .build();
        } else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.YI_YAN.getPlatform())) {
            return QianFanImageOptions.builder()
                    .model(model.getModel()).N(1)
                    .height(draw.getHeight()).width(draw.getWidth())
                    .build();
        } else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.ZHI_PU.getPlatform())) {
            return ZhiPuAiImageOptions.builder()
                    .model(model.getModel())
                    .build();
        }
        throw new IllegalArgumentException("不支持的 AI 平台：" + model.getPlatform());
    }

    /**
     * 从 ImageResponse 中提取图片 URL
     * - OpenAI/Gitee AI: 返回 b64_json（Base64 编码的图片数据）
     * - 硅基流动/其他: 返回 url（图片 URL）
     */
    private String extractImageUrl(ImageResponse response, AiModelDO model) {
        String url = response.getResult().getOutput().getUrl();
        String b64Json = response.getResult().getOutput().getB64Json();

        // 如果有 URL，直接返回
        if (StrUtil.isNotBlank(url)) {
            return url;
        }

        // 如果有 Base64 数据，上传到存储服务
        if (StrUtil.isNotBlank(b64Json)) {
            try {
                byte[] imageData = Base64.decode(b64Json);
                String imageUrl = uploadImageToStorage(imageData);
                log.info("[extractImageUrl][平台({}) Base64 图片上传成功，url: {}]", model.getPlatform(), imageUrl);
                return imageUrl;
            } catch (Exception e) {
                log.error("[extractImageUrl][平台({}) Base64 图片上传失败]", model.getPlatform(), e);
                throw new RuntimeException("图片上传失败: " + e.getMessage(), e);
            }
        }

        throw new IllegalArgumentException("图片生成结果为空（无 URL 也无 Base64 数据）");
    }

    /**
     * 上传图片到存储服务（七牛云）
     */
    private String uploadImageToStorage(byte[] imageData) {
        try {
            String accessKey = sysConfigService.get("qnAccessKey");
            String secretKey = sysConfigService.get("qnSecretKey");
            String bucket = sysConfigService.get("qnStorageName");
            String cdnDomain = sysConfigService.get("qnStorageCDN");

            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            Configuration cfg = new Configuration(Region.autoRegion());
            UploadManager uploadManager = new UploadManager(cfg);

            String fileName = "ai/image/" + IdUtil.fastSimpleUUID() + ".png";

            // 上传到七牛云
            Response response = uploadManager.put(imageData, fileName, upToken);

            if (response.isOK()) {
                String url = cdnDomain;
                if (!url.endsWith("/")) {
                    url += "/";
                }
                url += fileName;
                log.info("[uploadImageToStorage][图片上传成功，fileName: {}, url: {}]", fileName, url);
                return url;
            } else {
                log.error("[uploadImageToStorage][图片上传失败，response: {}]", response.bodyString());
                throw new RuntimeException("七牛云上传失败: " + response.bodyString());
            }
        } catch (Exception e) {
            log.error("[uploadImageToStorage][图片上传异常]", e);
            throw new RuntimeException("七牛云上传异常: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteImageMy(Long id, Long userId) {
        // 1. 校验是否存在
        AiImageDO image = validateImageExists(id);
        if (ObjUtil.notEqual(image.getUserId(), userId)) {
            throw exception(IMAGE_NOT_EXISTS);
        }
        // 2. 删除记录
        imageMapper.deleteById(id);
    }

    @Override
    public PageResult<AiImageDO> getImagePage(AiImagePageReqVO pageReqVO) {
        return imageMapper.selectPage(pageReqVO);
    }

    @Override
    public void updateImage(AiImageUpdateReqVO updateReqVO) {
        // 1. 校验存在
        validateImageExists(updateReqVO.getId());
        // 2. 更新发布状态
        imageMapper.updateById(BeanUtils.toBean(updateReqVO, AiImageDO.class));
    }

    @Override
    public void deleteImage(Long id) {
        // 1. 校验存在
        validateImageExists(id);
        // 2. 删除
        imageMapper.deleteById(id);
    }

    private AiImageDO validateImageExists(Long id) {
        AiImageDO image = imageMapper.selectById(id);
        if (image == null) {
            throw exception(IMAGE_NOT_EXISTS);
        }
        return image;
    }

    // ================ midjourney 专属 ================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long midjourneyImagine(Long userId, AiMidjourneyImagineReqVO drawReqVO) {
        // 1. 校验模型
        AiModelDO model = modelService.validateModel(drawReqVO.getModelId());
        Assert.equals(model.getPlatform(), AiPlatformEnum.MIDJOURNEY.getPlatform(), "平台不匹配");
        MidjourneyApi midjourneyApi = modelService.getMidjourneyApi(model.getId());

        // 2. 保存数据库
        AiImageDO image = BeanUtils.toBean(drawReqVO, AiImageDO.class).setUserId(userId).setPublicStatus(false)
                .setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .setPlatform(AiPlatformEnum.MIDJOURNEY.getPlatform()).setModelId(model.getId()).setModel(model.getName());
        imageMapper.insert(image);

        // 3. 调用 Midjourney Proxy 提交任务
        List<String> base64Array = StrUtil.isBlank(drawReqVO.getReferImageUrl()) ? null :
                Collections.singletonList("data:image/jpeg;base64,".concat(Base64.encode(HttpUtil.downloadBytes(drawReqVO.getReferImageUrl()))));
        MidjourneyApi.ImagineRequest imagineRequest = new MidjourneyApi.ImagineRequest(
                base64Array, drawReqVO.getPrompt(),null,
                MidjourneyApi.ImagineRequest.buildState(drawReqVO.getWidth(),
                        drawReqVO.getHeight(), drawReqVO.getVersion(), model.getModel()));
        MidjourneyApi.SubmitResponse imagineResponse = midjourneyApi.imagine(imagineRequest);

        // 4.1 情况一【失败】：抛出业务异常
        if (!MidjourneyApi.SubmitCodeEnum.SUCCESS_CODES.contains(imagineResponse.code())) {
            String description = imagineResponse.description().contains("quota_not_enough") ?
                    "账户余额不足" : imagineResponse.description();
            throw exception(IMAGE_MIDJOURNEY_SUBMIT_FAIL, description);
        }

        // 4.2 情况二【成功】：更新 taskId 和参数
        imageMapper.updateById(new AiImageDO().setId(image.getId())
                .setTaskId(imagineResponse.result()).setOptions(BeanUtil.beanToMap(drawReqVO)));
        return image.getId();
    }

    @Override
    public Integer midjourneySync() {
        // 1.1 获取 Midjourney 平台，状态在 “进行中” 的 image
        List<AiImageDO> images = imageMapper.selectListByStatusAndPlatform(
                AiImageStatusEnum.IN_PROGRESS.getStatus(), AiPlatformEnum.MIDJOURNEY.getPlatform());
        if (CollUtil.isEmpty(images)) {
            return 0;
        }
        // 1.2 调用 Midjourney Proxy 获取任务进展
        MidjourneyApi midjourneyApi = modelService.getMidjourneyApi(images.get(0).getModelId());
        List<MidjourneyApi.Notify> taskList = midjourneyApi.getTaskList(convertSet(images, AiImageDO::getTaskId));
        Map<String, MidjourneyApi.Notify> taskMap = convertMap(taskList, MidjourneyApi.Notify::id);

        // 2. 逐个处理，更新进展
        int count = 0;
        for (AiImageDO image : images) {
            MidjourneyApi.Notify notify = taskMap.get(image.getTaskId());
            if (notify == null) {
                log.error("[midjourneySync][image({}) 查询不到进展]", image);
                continue;
            }
            count++;
            updateMidjourneyStatus(image, notify);
        }
        return count;
    }

    @Override
    public void midjourneyNotify(MidjourneyApi.Notify notify) {
        // 1. 校验 image 存在
        AiImageDO image = imageMapper.selectByTaskId(notify.id());
        if (image == null) {
            log.warn("[midjourneyNotify][回调任务({}) 不存在]", notify.id());
            return;
        }
        // 2. 更新状态
        updateMidjourneyStatus(image, notify);
    }

    private void updateMidjourneyStatus(AiImageDO image, MidjourneyApi.Notify notify) {
        // 1. 转换状态
        Integer status = null;
        LocalDateTime finishTime = null;
        if (StrUtil.isNotBlank(notify.status())) {
            MidjourneyApi.TaskStatusEnum taskStatusEnum = MidjourneyApi.TaskStatusEnum.valueOf(notify.status());
            if (MidjourneyApi.TaskStatusEnum.SUCCESS == taskStatusEnum) {
                status = AiImageStatusEnum.SUCCESS.getStatus();
                finishTime = LocalDateTime.now();
            } else if (MidjourneyApi.TaskStatusEnum.FAILURE == taskStatusEnum) {
                status = AiImageStatusEnum.FAIL.getStatus();
                finishTime = LocalDateTime.now();
            }
        }

        // 2. 上传图片
        String picUrl = null;
//        if (StrUtil.isNotBlank(notify.imageUrl())) {
//            try {
//                picUrl = fileApi.createFile(HttpUtil.downloadBytes(notify.imageUrl()));
//            } catch (Exception e) {
//                picUrl = notify.imageUrl();
//                log.warn("[updateMidjourneyStatus][图片({}) 地址({}) 上传失败]", image.getId(), notify.imageUrl(), e);
//            }
//        }

        // 3. 更新 image 状态
        imageMapper.updateById(new AiImageDO().setId(image.getId()).setStatus(status)
                .setPicUrl(picUrl).setButtons(notify.buttons()).setErrorMessage(notify.failReason())
                .setFinishTime(finishTime));
    }

    @Override
    public Long midjourneyAction(Long userId, AiMidjourneyActionReqVO reqVO) {
        // 1.1 检查 image
        AiImageDO image = validateImageExists(reqVO.getId());
        if (ObjUtil.notEqual(userId, image.getUserId())) {
            throw exception(IMAGE_NOT_EXISTS);
        }
        MidjourneyApi midjourneyApi = modelService.getMidjourneyApi(image.getModelId());
        // 1.2 检查 customId
        MidjourneyApi.Button button = CollUtil.findOne(image.getButtons(),
                buttonX -> buttonX.customId().equals(reqVO.getCustomId()));
        if (button == null) {
            throw exception(IMAGE_CUSTOM_ID_NOT_EXISTS);
        }

        // 2. 调用 Midjourney Proxy 提交任务
        MidjourneyApi.SubmitResponse actionResponse = midjourneyApi.action(
                new MidjourneyApi.ActionRequest(button.customId(), image.getTaskId(), null));
        if (!MidjourneyApi.SubmitCodeEnum.SUCCESS_CODES.contains(actionResponse.code())) {
            String description = actionResponse.description().contains("quota_not_enough") ?
                    "账户余额不足" : actionResponse.description();
            throw exception(IMAGE_MIDJOURNEY_SUBMIT_FAIL, description);
        }

        // 3. 新增 image 记录
        AiImageDO newImage = new AiImageDO().setUserId(image.getUserId()).setPublicStatus(false).setPrompt(image.getPrompt())
                .setStatus(AiImageStatusEnum.IN_PROGRESS.getStatus())
                .setPlatform(AiPlatformEnum.MIDJOURNEY.getPlatform())
                .setModel(image.getModel()).setWidth(image.getWidth()).setHeight(image.getHeight())
                .setOptions(image.getOptions()).setTaskId(actionResponse.result());
        imageMapper.insert(newImage);
        return newImage.getId();
    }

    /**
     * 创建聊天消息
     * 将生成的图片保存到聊天消息中
     *
     * @param image 图片记录
     * @param picUrl 图片URL
     */
    private void createChatMessage(AiImageDO image, String picUrl) {
        try {
            // 如果没有 conversationId,则不创建聊天消息
            if (image.getConversationId() == null) {
                log.info("[createChatMessage] 图片生成成功，但未关联对话，imageId={}, picUrl={}", image.getId(), picUrl);
                return;
            }

            // 直接存储图片 URL，不使用 Markdown 格式
            // 前端会根据 msgType 字段判断是图片类型，自动渲染为图片
            // msgType 会根据模型类型自动确定，无需手动传入

            // 调用 chatMessageService 保存消息
            var result = chatMessageService.saveGeneratedContent(
                    image.getConversationId(),
                    image.getPrompt(),
                    picUrl,  // 直接存储 URL
                    image.getUserId(),
                    null
            );

            // 更新 image 记录，保存 chatMessageId
            imageMapper.updateById(new AiImageDO().setId(image.getId()).setChatMessageId(result.getReceive().getId()));

            log.info("[createChatMessage] 图片消息已保存到数据库，imageId={}, chatMessageId={}", image.getId(), result.getReceive().getId());
        } catch (Exception e) {
            log.error("[createChatMessage] 创建聊天消息失败，imageId={}", image.getId(), e);
        }
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private AiImageServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
