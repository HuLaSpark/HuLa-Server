package com.luohuo.flex.base.service.system;

import com.luohuo.basic.base.service.SuperService;
import com.luohuo.flex.base.entity.system.DefLoginLog;
import com.luohuo.flex.base.service.system.dto.LoginCountDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 登录日志
 * </p>
 *
 * @author 乾乾
 * @date 2021-11-12
 */
public interface DefLoginLogService extends SuperService<Long, DefLoginLog> {
    /**
     * 清理日志
     *
     * @param clearBeforeTime 多久之前的
     * @param clearBeforeNum  多少条
     * @return 是否成功
     */
    boolean clearLog(LocalDateTime clearBeforeTime, Integer clearBeforeNum);

    List<LoginCountDTO> getLoginRank(LocalDateTime start, LocalDateTime end, Integer limit);

    Long countUsersWithMinLogins(LocalDateTime start, LocalDateTime end, Integer minTimes);
}
