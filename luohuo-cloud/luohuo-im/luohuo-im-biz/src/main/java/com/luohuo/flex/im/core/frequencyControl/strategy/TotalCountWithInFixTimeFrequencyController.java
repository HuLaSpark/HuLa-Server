package com.luohuo.flex.im.core.frequencyControl.strategy;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.flex.im.core.frequencyControl.constant.FrequencyControlConstant;
import com.luohuo.flex.im.core.frequencyControl.dto.FixedWindowDTO;
import com.luohuo.flex.im.core.frequencyControl.factory.AbstractFrequencyControlService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 抽象类频控服务 -使用redis实现 固定时间内不超过固定次数的限流类
 * @author nyh
 */
@Slf4j
@Service
public class TotalCountWithInFixTimeFrequencyController extends AbstractFrequencyControlService<FixedWindowDTO> {

	@Resource
	private CachePlusOps cachePlusOps;
    /**
     * 是否达到限流阈值 子类实现 每个子类都可以自定义自己的限流逻辑判断
     *
     * @param frequencyControlMap 定义的注解频控 Map中的Key-对应redis的单个频控的Key Map中的Value-对应redis的单个频控的Key限制的Value
     * @return true-方法被限流 false-方法没有被限流
     */
    @Override
    protected boolean reachRateLimit(Map<String, FixedWindowDTO> frequencyControlMap) {
        //批量获取redis统计的值
        List<String> frequencyKeys = new ArrayList<>(frequencyControlMap.keySet());
        List<Integer> countList = cachePlusOps.mGet(frequencyKeys, Integer.class);
        for (int i = 0; i < frequencyKeys.size(); i++) {
            String key = frequencyKeys.get(i);
            Integer count = countList.get(i);
            int frequencyControlCount = frequencyControlMap.get(key).getCount();
            if (Objects.nonNull(count) && count >= frequencyControlCount) {
                //频率超过了
                log.warn("frequencyControl limit key:{},count:{}", key, count);
                return true;
            }
        }
        return false;
    }

    /**
     * 增加限流统计次数 子类实现 每个子类都可以自定义自己的限流统计信息增加的逻辑
     *
     * @param frequencyControlMap 定义的注解频控 Map中的Key-对应redis的单个频控的Key Map中的Value-对应redis的单个频控的Key限制的Value
     */
    @Override
    protected void addFrequencyControlStatisticsCount(Map<String, FixedWindowDTO> frequencyControlMap) {
        frequencyControlMap.forEach((k, v) -> cachePlusOps.inc(k, v.getTime(), v.getUnit()));
    }

    @Override
    protected String getStrategyName() {
        return FrequencyControlConstant.TOTAL_COUNT_WITH_IN_FIX_TIME;
    }
}
