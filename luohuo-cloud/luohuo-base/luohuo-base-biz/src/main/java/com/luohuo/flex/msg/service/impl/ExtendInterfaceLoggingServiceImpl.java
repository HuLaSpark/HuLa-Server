package com.luohuo.flex.msg.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.service.impl.SuperServiceImpl;

import com.luohuo.flex.msg.entity.ExtendInterfaceLogging;
import com.luohuo.flex.msg.manager.ExtendInterfaceLoggingManager;
import com.luohuo.flex.msg.service.ExtendInterfaceLoggingService;

/**
 * <p>
 * 业务实现类
 * 接口执行日志记录
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-09 23:58:59
 * @create [2022-07-09 23:58:59] [zuihou] [代码生成器生成]
 */

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ExtendInterfaceLoggingServiceImpl extends SuperServiceImpl<ExtendInterfaceLoggingManager, Long, ExtendInterfaceLogging> implements ExtendInterfaceLoggingService {


}


