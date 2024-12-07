package com.hula.core.user.domain.vo.req.user;

import com.hula.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 批量查询用户汇总详情
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummeryInfoReq extends BaseEntity {
    @ApiModelProperty(value = "用户信息入参")
    @Size(max = 50)
    private List<infoReq> reqList;

    @Data
    public static class infoReq {
        @ApiModelProperty(value = "uid")
        private Long uid;
        @ApiModelProperty(value = "最近一次更新用户信息时间")
        private Long lastModifyTime;
    }
}
