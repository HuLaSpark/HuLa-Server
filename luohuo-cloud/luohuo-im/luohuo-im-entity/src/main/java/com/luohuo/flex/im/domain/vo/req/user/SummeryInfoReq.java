package com.luohuo.flex.im.domain.vo.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * 批量查询用户汇总详情
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SummeryInfoReq implements Serializable {
	@Schema(description = "用户信息入参")
    @Size(max = 50, message = "一次最多查询50个用户数据")
    private List<infoReq> reqList;

    @Data
    public static class infoReq {
		@Schema(description = "uid")
        private Long uid;
		@Schema(description = "最近一次更新用户信息时间")
        private Long lastModifyTime;
    }
}
