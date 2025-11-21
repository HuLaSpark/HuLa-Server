package com.luohuo.flex.ai.service.video;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.video.vo.AiVideoGenerateReqVO;
import com.luohuo.flex.ai.controller.video.vo.AiVideoPageReqVO;
import com.luohuo.flex.ai.controller.video.vo.AiVideoUpdateReqVO;
import com.luohuo.flex.ai.controller.video.vo.GiteeAiVideoNotifyVO;
import com.luohuo.flex.ai.core.model.gitee.GiteeAiVideoOptions;
import com.luohuo.flex.ai.core.model.silicon.SiliconFlowVideoOptions;
import com.luohuo.flex.ai.core.model.video.VideoModel;
import com.luohuo.flex.ai.core.model.video.VideoOptions;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.dal.video.AiVideoDO;
import com.luohuo.flex.ai.enums.AiPlatformEnum;
import com.luohuo.flex.ai.enums.AiVideoStatusEnum;
import com.luohuo.flex.ai.mapper.video.AiVideoMapper;
import com.luohuo.flex.ai.service.chat.AiChatMessageService;
import com.luohuo.flex.ai.service.model.AiModelService;
import com.luohuo.flex.ai.service.model.AiModelUsageService;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.flex.common.constant.DefValConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static com.luohuo.flex.ai.enums.ErrorCodeConstants.VIDEO_NOT_EXISTS;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;

@Service
@Slf4j
public class AiVideoServiceImpl implements AiVideoService {

	@Resource
	private AiModelService modelService;

	@Resource
	private AiVideoMapper videoMapper;

	@Resource
	private AiModelUsageService modelUsageService;

	@Resource
	private AiChatMessageService chatMessageService;

    @Resource(name = "videoTaskScheduler")
    private TaskScheduler videoTaskScheduler;

    private final ConcurrentHashMap<Long, ScheduledFuture<?>> videoPollers = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Long, AtomicInteger> videoAttempts = new ConcurrentHashMap<>();

	@Override
	public PageResult<AiVideoDO> getVideoPageMy(Long userId, AiVideoPageReqVO pageReqVO) {
		return videoMapper.selectPageMy(userId, pageReqVO);
	}

	@Override
	public AiVideoDO getVideo(Long id) {
		return videoMapper.selectById(id);
	}

	@Override
	public List<AiVideoDO> getVideoList(List<Long> ids) {
		if (CollUtil.isEmpty(ids)) {
			return Collections.emptyList();
		}
		return videoMapper.selectBatchIds(ids);
	}

	@Override
	public Long generateVideo(Long userId, AiVideoGenerateReqVO generateReqVO) {
		// 1. 校验模型
		AiModelDO model = modelService.validateModel(generateReqVO.getModelId());

		// 2. 检查并扣减模型使用次数
		modelUsageService.checkAndDeductUsage(userId, model);

		// 3. 保存数据库
		AiVideoDO video = BeanUtils.toBean(generateReqVO, AiVideoDO.class).setUserId(userId)
				.setPlatform(model.getPlatform()).setModelId(model.getId()).setModel(model.getModel())
				.setPublicStatus(false).setStatus(AiVideoStatusEnum.IN_PROGRESS.getStatus());
		videoMapper.insert(video);

		executeGenerateVideo(video, generateReqVO, model);
		return video.getId();
	}

	@Async("videoTaskExecutor")
	public void executeGenerateVideo(AiVideoDO video, AiVideoGenerateReqVO reqVO, AiModelDO model) {
		try {
			VideoOptions options = buildVideoOptions(reqVO, model);

			VideoModel videoModel = modelService.getVideoModel(model.getId());

			String requestId = videoModel.submitVideo(reqVO.getPrompt(), options);
			videoMapper.updateById(new AiVideoDO().setId(video.getId()).setTaskId(requestId));
			video.setTaskId(requestId);
			startPolling(video);
		} catch (Exception ex) {
			log.error("[executeGenerateVideo][video({}) 生成失败]", video, ex);
			videoMapper.updateById(new AiVideoDO().setId(video.getId())
					.setStatus(AiVideoStatusEnum.FAIL.getStatus())
					.setErrorMessage(ex.getMessage()).setFinishTime(LocalDateTime.now()));
		}
	}

	/**
	 * 构建视频生成参数
	 */
	private static VideoOptions buildVideoOptions(AiVideoGenerateReqVO reqVO, AiModelDO model) {
		if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.SILICON_FLOW.getPlatform())) {
			// 硅基流动视频生成
			// https://docs.siliconflow.cn/cn/api-reference/videos/videos_submit

			// 根据宽高计算 image_size
			String imageSize = buildImageSize(reqVO.getWidth(), reqVO.getHeight());

			return SiliconFlowVideoOptions.builder()
					.model(model.getModel())
					.imageSize(imageSize)
					.width(reqVO.getWidth())
					.height(reqVO.getHeight())
					.duration(reqVO.getDuration())
					.negativePrompt(MapUtil.getStr(reqVO.getOptions(), "negativePrompt"))
					.seed(MapUtil.getInt(reqVO.getOptions(), "seed"))
					.image(MapUtil.getStr(reqVO.getOptions(), "image")).build();
		}
		else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.GITEE_AI.getPlatform())) {
			// https://ai.gitee.com/docs/openapi/v1#tag/%E8%A7%86%E9%A2%91%E7%94%9F%E6%88%90/post/async/videos/generations
			return GiteeAiVideoOptions.builder()
					.model(model.getModel())
					.width(reqVO.getWidth())
					.height(reqVO.getHeight())
					.duration(reqVO.getDuration())
					.negativePrompt(MapUtil.getStr(reqVO.getOptions(), "negativePrompt"))
					.seed(MapUtil.getInt(reqVO.getOptions(), "seed"))
					.image(MapUtil.getStr(reqVO.getOptions(), "image"))
					.build();
		}

		// TODO: 其他平台的 VideoOptions 构建逻辑
		// else if (ObjUtil.equal(model.getPlatform(), AiPlatformEnum.DEEP_SEEK.getPlatform())) {
		//     return DeepSeekVideoOptions.builder()...
		// }

		throw new IllegalArgumentException("不支持的 AI 平台：" + model.getPlatform());
	}

	private static String buildImageSize(Integer width, Integer height) {
		if (width == null || height == null) {
			return "1280x720";
		}

		if (width > height) {
			return "1280x720";
		} else if (height > width) {
			return "720x1280";
		} else {
			return "960x960";
		}
	}

	@Override
	public void deleteVideoMy(Long id, Long userId) {
		AiVideoDO video = videoMapper.selectById(id);
		if (video == null) {
			throw exception(VIDEO_NOT_EXISTS);
		}
		if (ObjUtil.notEqual(video.getUserId(), userId)) {
			throw exception(VIDEO_NOT_EXISTS);
		}
		videoMapper.deleteById(id);
	}

	@Override
	public PageResult<AiVideoDO> getVideoPage(AiVideoPageReqVO pageReqVO) {
		return videoMapper.selectPage(pageReqVO);
	}

	@Override
	public void updateVideo(AiVideoUpdateReqVO updateReqVO) {
		validateVideoExists(updateReqVO.getId());
		AiVideoDO updateObj = BeanUtils.toBean(updateReqVO, AiVideoDO.class);
		videoMapper.updateById(updateObj);
	}

	@Override
	public void deleteVideo(Long id) {
		validateVideoExists(id);
		videoMapper.deleteById(id);
	}

	private void validateVideoExists(Long id) {
		if (videoMapper.selectById(id) == null) {
			throw exception(VIDEO_NOT_EXISTS);
		}
	}

	@Override
	@Async
	public Long recoverIncompleteVideos() {
		ContextUtil.setTenantId(DefValConstants.DEF_TENANT_ID);
		List<AiVideoDO> incompleteVideos = videoMapper.selectListByStatusWithTaskId(AiVideoStatusEnum.IN_PROGRESS.getStatus());

		// 遍历所有未完成的视频，尝试恢复
		for (AiVideoDO video : incompleteVideos) {
			try {
				SpringUtils.getBean(AiVideoService.class).recoverVideo(video.getId());
			} catch (Exception ex) {
			}
		}
		ContextUtil.clearTenantContext();
		return (long) incompleteVideos.size();
	}

	@Override
	@Async("videoTaskExecutor")
	public void recoverVideo(Long videoId) {
		try {
			AiVideoDO video = videoMapper.selectById(videoId);
			if (video == null || video.getTaskId() == null) {
				log.warn("[recoverVideo] 视频 {} 不存在或没有 taskId", videoId);
				return;
			}
			startPolling(video);
		} catch (Exception ex) {
			log.error("[recoverVideo][video({}) 恢复失败]", videoId, ex);
			videoMapper.updateById(new AiVideoDO().setId(videoId)
					.setStatus(AiVideoStatusEnum.FAIL.getStatus())
					.setErrorMessage(ex.getMessage())
					.setFinishTime(LocalDateTime.now()));
		}
	}

	private void startPolling(AiVideoDO video) {
		ScheduledFuture<?> future = videoTaskScheduler.scheduleAtFixedRate(() -> {
			try {
				AtomicInteger counter = videoAttempts.computeIfAbsent(video.getId(), k -> new AtomicInteger(0));
				int c = counter.incrementAndGet();
				VideoModel videoModel = modelService.getVideoModel(video.getModelId());
				VideoModel.VideoStatusResponse status = videoModel.getVideoStatus(video.getTaskId());
				List<String> urls = status.getVideoUrls();
				if (urls != null && !urls.isEmpty()) {
					String url = urls.get(0);
					videoMapper.updateById(new AiVideoDO().setId(video.getId())
							.setStatus(AiVideoStatusEnum.SUCCESS.getStatus())
							.setVideoUrl(url)
							.setFinishTime(LocalDateTime.now()));
					try {
						createChatMessage(video, url);
					} catch (Exception ignored) {}
					ScheduledFuture<?> f = videoPollers.get(video.getId());
					if (f != null) f.cancel(false);
					videoPollers.remove(video.getId());
					videoAttempts.remove(video.getId());
				} else {
					String s = status.getStatus();
					if (s != null) {
						String sl = s.toLowerCase();
						if (sl.contains("fail") || sl.contains("error") || sl.contains("cancel")) {
							videoMapper.updateById(new AiVideoDO().setId(video.getId())
									.setStatus(AiVideoStatusEnum.FAIL.getStatus())
									.setErrorMessage(status.getMessage())
									.setFinishTime(LocalDateTime.now()));
							ScheduledFuture<?> f = videoPollers.get(video.getId());
							if (f != null) f.cancel(false);
							videoPollers.remove(video.getId());
							videoAttempts.remove(video.getId());
							return;
						}
					}
					if (c >= 120) {
						videoMapper.updateById(new AiVideoDO().setId(video.getId())
								.setStatus(AiVideoStatusEnum.FAIL.getStatus())
								.setErrorMessage("timeout")
								.setFinishTime(LocalDateTime.now()));
						ScheduledFuture<?> f = videoPollers.get(video.getId());
						if (f != null) f.cancel(false);
						videoPollers.remove(video.getId());
						videoAttempts.remove(video.getId());
					}
				}
			} catch (Exception ignored) {}
		}, 10000);
		videoPollers.put(video.getId(), future);
	}

	/**
	 * 如果有会话，创建对话消息
	 * 将生成的视频添加到对话记录中
	 *
	 * @param video    视频对象
	 * @param videoUrl 视频URL
	 */
	private void createChatMessage(AiVideoDO video, String videoUrl) {
		try {
			if (video.getConversationId() == null) {
				log.info("[createChatMessage] 视频生成成功但没有关联会话，跳过创建对话消息，videoId={}, videoUrl={}", video.getId(), videoUrl);
				return;
			}

			// 直接存储视频 URL，不使用 Markdown 格式
			// 前端会根据 msgType 字段判断是视频类型，自动渲染为视频播放器
			// msgType 会根据模型类型自动确定，无需手动传入

			var result = chatMessageService.saveGeneratedContent(
					video.getConversationId(),
					video.getPrompt(),
					videoUrl,  // 直接存储 URL
					video.getUserId(),
					null
			);

			// 更新 video 记录关联的 chatMessageId
			videoMapper.updateById(new AiVideoDO().setId(video.getId()).setChatMessageId(result.getReceive().getId()));

			log.info("[createChatMessage] 视频对话消息创建成功，videoId={}, chatMessageId={}", video.getId(), result.getReceive().getId());
		} catch (Exception e) {
			log.error("[createChatMessage] 如果有会话，创建对话消息失败，videoId={}", video.getId(), e);
		}
	}

	@Override
	public void giteeAiNotify(GiteeAiVideoNotifyVO notify) {
		// 1. 校验 video 存在
		AiVideoDO video = videoMapper.selectByTaskId(notify.getTaskId());
		if (video == null) {
			log.warn("[giteeAiNotify][回调任务({}) 不存在]", notify.getTaskId());
			return;
		}

		log.info("[giteeAiNotify][收到 Gitee AI 回调通知] taskId={}, status={}",
				notify.getTaskId(), notify.getStatus());

		// 2. 更新状态
		updateGiteeAiVideoStatus(video, notify);
	}

	/**
	 * 更新 Gitee AI 视频状态
	 * 根据官方文档状态：waiting / in_progress / success / failure / cancelled
	 *
	 * @param video  视频对象
	 * @param notify 回调通知数据
	 */
	private void updateGiteeAiVideoStatus(AiVideoDO video, GiteeAiVideoNotifyVO notify) {
		// 1. 转换状态
		Integer status = null;
		String videoUrl = null;
		LocalDateTime finishTime = null;

		if (StrUtil.isNotBlank(notify.getStatus())) {
			String notifyStatus = notify.getStatus().toLowerCase();

			// 成功状态（官方文档：success）
			if ("success".equals(notifyStatus)) {
				status = AiVideoStatusEnum.SUCCESS.getStatus();
				finishTime = LocalDateTime.now();

				// 提取视频 URL（注意：链接仅 7 天有效）
				if (notify.getOutput() != null && StrUtil.isNotBlank(notify.getOutput().getVideoUrl())) {
					videoUrl = notify.getOutput().getVideoUrl();
					log.info("[updateGiteeAiVideoStatus][视频生成成功，URL 7天有效] videoId={}, videoUrl={}",
							video.getId(), videoUrl);
				} else {
					log.warn("[updateGiteeAiVideoStatus][视频生成成功但 URL 为空] videoId={}", video.getId());
				}
			}
			// 失败状态（官方文档：failure, cancelled）
			else if ("failure".equals(notifyStatus) || "cancelled".equals(notifyStatus)) {
				status = AiVideoStatusEnum.FAIL.getStatus();
				finishTime = LocalDateTime.now();
				log.warn("[updateGiteeAiVideoStatus][视频生成失败] videoId={}, status={}",
						video.getId(), notifyStatus);
			}
			// 处理中状态（官方文档：waiting, in_progress）
			else if ("waiting".equals(notifyStatus) || "in_progress".equals(notifyStatus)) {
				log.info("[updateGiteeAiVideoStatus][视频生成中] videoId={}, status={}",
						video.getId(), notifyStatus);
				// 处理中状态不更新数据库，等待最终状态
				return;
			}
		}

		// 2. 更新数据库
		if (status != null) {
			AiVideoDO updateVideo = new AiVideoDO()
					.setId(video.getId())
					.setStatus(status)
					.setFinishTime(finishTime);

			if (StrUtil.isNotBlank(videoUrl)) {
				updateVideo.setVideoUrl(videoUrl);
			}

			videoMapper.updateById(updateVideo);
			log.info("[updateGiteeAiVideoStatus][视频({}) 状态更新成功] status={}, videoUrl={}",
					video.getId(), status, videoUrl);

			// 3. 如果生成成功且有视频 URL，创建对话消息
			if (status.equals(AiVideoStatusEnum.SUCCESS.getStatus()) && StrUtil.isNotBlank(videoUrl)) {
				try {
					createChatMessage(video, videoUrl);
				} catch (Exception e) {
					log.error("[updateGiteeAiVideoStatus] 创建对话消息失败，videoId={}", video.getId(), e);
				}
			}
		}
	}
}

