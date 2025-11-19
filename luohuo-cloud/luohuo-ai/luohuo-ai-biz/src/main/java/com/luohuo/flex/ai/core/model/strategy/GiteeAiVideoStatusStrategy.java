package com.luohuo.flex.ai.core.model.strategy;

/**
 * Gitee AI 视频生成状态判断策略
 *
 * <p>根据 Gitee AI 官方文档的状态定义，判断视频生成任务的状态。</p>
 *
 * <p>官方文档状态说明：</p>
 * <ul>
 *   <li>waiting - 任务等待中</li>
 *   <li>in_progress - 任务处理中</li>
 *   <li>success - 任务成功</li>
 *   <li>failure - 任务失败</li>
 *   <li>cancelled - 任务已取消</li>
 * </ul>
 *
 * @author 乾乾
 */
public class GiteeAiVideoStatusStrategy implements VideoStatusStrategy {

	@Override
	public boolean isSuccessStatus(String status) {
		// 官方文档：success
		return "success".equalsIgnoreCase(status);
	}

	@Override
	public boolean isFailureStatus(String status) {
		// 官方文档：failure, cancelled
		return "failure".equalsIgnoreCase(status) ||
				"cancelled".equalsIgnoreCase(status);
	}

	@Override
	public boolean isProcessingStatus(String status) {
		// 官方文档：waiting, in_progress
		return "waiting".equalsIgnoreCase(status) ||
				"in_progress".equalsIgnoreCase(status);
	}

	@Override
	public String getPlatformName() {
		return "GiteeAI";
	}
}