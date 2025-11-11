package com.luohuo.flex.ai.service.platform;

import cn.hutool.core.util.StrUtil;
import com.luohuo.flex.ai.dal.platform.AiPlatformDO;
import com.luohuo.flex.ai.mapper.platform.AiPlatformMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AI 平台配置 Service 实现类
 *
 * @author 乾乾
 */
@Service
@Validated
public class AiPlatformServiceImpl implements AiPlatformService {

    @Resource
    private AiPlatformMapper platformMapper;

    @Override
    public List<AiPlatformDO> getPlatformList() {
        return platformMapper.selectListByStatus();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addModelToExamples(String platform, String model) {
        // 查询平台配置
        AiPlatformDO platformDO = platformMapper.selectOne(AiPlatformDO::getPlatform, platform);
        if (platformDO == null) {
            throw new IllegalArgumentException("平台不存在: " + platform);
        }

        String examples = platformDO.getExamples();

        // 去重处理
        Set<String> modelSet = new LinkedHashSet<>();
        if (StrUtil.isNotBlank(examples)) {
            modelSet.addAll(Arrays.stream(examples.split(","))
                    .map(String::trim)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList()));
        }

        // 添加新模型
        String trimmedModel = model.trim();
        boolean isNewModel = !modelSet.contains(trimmedModel);
        if (isNewModel) {
            modelSet.add(trimmedModel);
        }

        // 无论是否添加新模型
        String newExamples = String.join(", ", modelSet);

        // 只有当 examples 发生变化时才更新数据库
        if (!newExamples.equals(examples)) {
            platformDO.setExamples(newExamples);
            platformMapper.updateById(platformDO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanDuplicateModels() {
        // 获取所有平台
        List<AiPlatformDO> allPlatforms = platformMapper.selectList();
        int cleanedCount = 0;

        for (AiPlatformDO platform : allPlatforms) {
            String examples = platform.getExamples();
            if (StrUtil.isBlank(examples)) {
                continue;
            }

            // 将 examples 转换为 Set（自动去重 + 保持顺序）
            Set<String> modelSet = new LinkedHashSet<>();
            modelSet.addAll(Arrays.stream(examples.split(","))
                    .map(String::trim)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList()));

            // 重新生成 examples
            String newExamples = String.join(", ", modelSet);

            // 如果发生变化，更新数据库
            if (!newExamples.equals(examples)) {
                platform.setExamples(newExamples);
                platformMapper.updateById(platform);
                cleanedCount++;
            }
        }

        return cleanedCount;
    }

}
