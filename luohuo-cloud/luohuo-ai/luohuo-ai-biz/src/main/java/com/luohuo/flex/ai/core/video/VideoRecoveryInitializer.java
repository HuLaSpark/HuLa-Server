package com.luohuo.flex.ai.core.video;

import com.luohuo.flex.ai.service.video.AiVideoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VideoRecoveryInitializer implements ApplicationRunner {

    @Resource
    private AiVideoService videoService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("[VideoRecoveryInitializer] 开始检测未完成的视频任务...");
        try {
            Long count = videoService.recoverIncompleteVideos();
            if (count > 0) {
                log.info("[VideoRecoveryInitializer] 检测到 {} 个未完成的视频任务，已提交恢复", count);
            } else {
                log.info("[VideoRecoveryInitializer] 没有检测到未完成的视频任务");
            }
        } catch (Exception ex) {
            log.error("[VideoRecoveryInitializer] 恢复未完成的视频任务失败", ex);
        }
    }
}
