package com.luohuo.flex.base.manager.application.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.luohuo.basic.base.manager.impl.SuperManagerImpl;
import com.luohuo.flex.base.entity.application.DefUserApplication;
import com.luohuo.flex.base.manager.application.DefUserApplicationManager;
import com.luohuo.flex.base.mapper.application.DefUserApplicationMapper;

/**
 * <p>
 * 通用业务实现类
 * 用户的默认应用
 * </p>
 *
 * @author 乾乾
 * @date 2022-03-06
 * @create [2022-03-06] [zuihou] [代码生成器生成]
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefUserApplicationManagerImpl extends SuperManagerImpl<DefUserApplicationMapper, DefUserApplication> implements DefUserApplicationManager {
}
