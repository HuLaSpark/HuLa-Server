package com.luohuo.flex.base.mapper.system;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.luohuo.flex.base.service.system.dto.LoginCountDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.base.entity.system.DefLoginLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 登录日志
 * </p>
 *
 * @author 乾乾
 * @date 2021-11-12
 */
@Repository
@InterceptorIgnore(tenantLine = "true", dynamicTableName = "true")
public interface DefLoginLogMapper extends SuperMapper<DefLoginLog> {
    /**
     * 清理日志
     *
     * @param clearBeforeTime 多久之前的
     * @param idList          待删除
     * @return 是否成功
     */
    Long clearLog(@Param("clearBeforeTime") LocalDateTime clearBeforeTime, @Param("idList") List<Long> idList);

    Long selectUserCountWithMinLogins(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("minTimes") Integer minTimes);

    List<LoginCountDTO> selectLoginCountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("limit") Integer limit);
}
