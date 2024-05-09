package com.hula.common.domain.vo.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 基础翻页请求
 * @author nyh
 */
@Data
@Schema(description = "基础翻页请求")
public class PageBaseReq {

    @Schema(description = "页面大小")
    @Min(0)
    @Max(50)
    private Integer pageSize = 10;

    @Schema(description = "页面索引（从1开始）")
    private Integer pageNo = 1;

    /**
     * 获取mybatisPlus的page
     */
    public Page plusPage() {
        return new Page(pageNo, pageSize);
    }
}
