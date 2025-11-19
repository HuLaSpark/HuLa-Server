package com.luohuo.flex.im.core.frequencyControl.aspect;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.utils.SpElUtils;
import com.luohuo.flex.im.core.frequencyControl.annotation.FrequencyControl;
import com.luohuo.flex.im.core.frequencyControl.constant.FrequencyControlConstant;
import com.luohuo.flex.im.core.frequencyControl.dto.FixedWindowDTO;
import com.luohuo.flex.im.core.frequencyControl.dto.FrequencyControlDTO;
import com.luohuo.flex.im.core.frequencyControl.dto.SlidingWindowDTO;
import com.luohuo.flex.im.core.frequencyControl.dto.TokenBucketDTO;
import com.luohuo.flex.im.core.frequencyControl.util.FrequencyControlUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 频控实现
 *
 * @author nyh
 */
@Slf4j
@Aspect
@Component
public class FrequencyControlAspect {

    @Around("@annotation(com.luohuo.flex.im.core.frequencyControl.annotation.FrequencyControl) || @annotation(com.luohuo.flex.im.core.frequencyControl.annotation.FrequencyControlContainer)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        FrequencyControl[] annotationsByType = method.getAnnotationsByType(FrequencyControl.class);
        Map<String, FrequencyControl> keyMap = new HashMap<>();
        String strategy = FrequencyControlConstant.TOTAL_COUNT_WITH_IN_FIX_TIME;
        for (int i = 0; i < annotationsByType.length; i++) {
            // 获取频控注解
            FrequencyControl frequencyControl = annotationsByType[i];
            /* 默认方法限定名 + 注解排名（可能多个）*/
            String prefix = StrUtil.isBlank(frequencyControl.prefixKey()) ? method.toGenericString() + ":index:" + i : frequencyControl.prefixKey();
            String key = "";
            switch (frequencyControl.target()) {
                case EL:
                    key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), frequencyControl.spEl());
                    break;
                case IP:
                    key = ContextUtil.getIP();
                    break;
                case UID:
                    key = ContextUtil.getUid().toString();
            }
            keyMap.put(prefix + ":" + key, frequencyControl);
            strategy = frequencyControl.strategy();
        }
        // 将注解的参数转换为编程式调用需要的参数
        if (FrequencyControlConstant.TOTAL_COUNT_WITH_IN_FIX_TIME.equals(strategy)) {
            // 调用编程式注解 固定窗口
            List<FrequencyControlDTO> frequencyControlDTOS = keyMap.entrySet().stream().map(entrySet -> buildFixedWindowDTO(entrySet.getKey(), entrySet.getValue())).collect(Collectors.toList());
            return FrequencyControlUtil.executeWithFrequencyControlList(strategy, frequencyControlDTOS, joinPoint::proceed);

        } else if (FrequencyControlConstant.TOKEN_BUCKET.equals(strategy)) {
            // 调用编程式注解 令牌桶
            List<TokenBucketDTO> frequencyControlDTOS = keyMap.entrySet().stream().map(entrySet -> buildTokenBucketDTO(entrySet.getKey(), entrySet.getValue())).collect(Collectors.toList());
            return FrequencyControlUtil.executeWithFrequencyControlList(strategy, frequencyControlDTOS, joinPoint::proceed);
        } else {
            // 调用编程式注解 滑动窗口
            List<SlidingWindowDTO> frequencyControlDTOS = keyMap.entrySet().stream().map(entrySet -> buildSlidingWindowFrequencyControlDTO(entrySet.getKey(), entrySet.getValue())).collect(Collectors.toList());
            return FrequencyControlUtil.executeWithFrequencyControlList(strategy, frequencyControlDTOS, joinPoint::proceed);
        }
    }

    /**
     * 将注解参数转换为编程式调用所需要的参数
     *
     * @param key              频率控制Key
     * @param frequencyControl 注解
     * @return 编程式调用所需要的参数-FrequencyControlDTO
     */
    private SlidingWindowDTO buildSlidingWindowFrequencyControlDTO(String key, FrequencyControl frequencyControl) {
        SlidingWindowDTO frequencyControlDTO = new SlidingWindowDTO();
        frequencyControlDTO.setWindowSize(frequencyControl.windowSize());
        frequencyControlDTO.setPeriod(frequencyControl.period());
        frequencyControlDTO.setCount(frequencyControl.count());
        frequencyControlDTO.setUnit(frequencyControl.unit());
        frequencyControlDTO.setKey(key);
        return frequencyControlDTO;
    }

    /**
     * 将注解参数转换为编程式调用所需要的参数
     *
     * @param key              频率控制Key
     * @param frequencyControl 注解
     * @return 编程式调用所需要的参数-FrequencyControlDTO
     */
    private TokenBucketDTO buildTokenBucketDTO(String key, FrequencyControl frequencyControl) {
        TokenBucketDTO tokenBucketDTO = new TokenBucketDTO(frequencyControl.capacity(), frequencyControl.refillRate());
        tokenBucketDTO.setKey(key);
        return tokenBucketDTO;
    }

    /**
     * 将注解参数转换为编程式调用所需要的参数
     *
     * @param key              频率控制Key
     * @param frequencyControl 注解
     * @return 编程式调用所需要的参数-FrequencyControlDTO
     */
    private FixedWindowDTO buildFixedWindowDTO(String key, FrequencyControl frequencyControl) {
        FixedWindowDTO fixedWindowDTO = new FixedWindowDTO();
        fixedWindowDTO.setCount(frequencyControl.count());
        fixedWindowDTO.setTime(frequencyControl.time());
        fixedWindowDTO.setUnit(frequencyControl.unit());
        fixedWindowDTO.setKey(key);
        return fixedWindowDTO;
    }

}
