package com.luohuo.flex.im.facade;

import java.util.Map;
import java.util.Set;

/**
 * 资源
 * @author tangyh
 * @since 2024/9/21 22:20
 */
public interface DefResourceFacade {
    /** 查询系统的所有资源API */
    Map<String, Set<String>> listAllApi();
}
