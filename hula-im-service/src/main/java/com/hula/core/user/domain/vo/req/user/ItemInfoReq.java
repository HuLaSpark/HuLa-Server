package com.hula.core.user.domain.vo.req.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;


/**
 * 批量查询徽章详情
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoReq {
    @ApiModelProperty(value = "徽章信息入参")
    @Size(max = 50)
    private List<infoReq> reqList;

    @Data
    public static class infoReq {
        @ApiModelProperty(value = "徽章id")
        private Long itemId;
        @ApiModelProperty(value = "最近一次更新徽章信息时间")
        private Long lastModifyTime;
    }
}
