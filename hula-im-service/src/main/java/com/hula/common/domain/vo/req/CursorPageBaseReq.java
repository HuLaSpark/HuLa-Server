package com.hula.common.domain.vo.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 游标翻页请求
 * @author nyh
 */
@Data
@Schema(description = "游标翻页请求")
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseReq {

    @Schema(description = "页面大小")
    @Min(0)
    @Max(100)
    private Integer pageSize = 10;

    @Schema(description = "游标（初始为null，后续请求附带上次翻页的游标）")
    private String cursor;

    public Page plusPage() {
        return new Page(1, this.pageSize, false);
    }

    @JsonIgnore
    public Boolean isFirstPage() {
        return StringUtils.isEmpty(cursor);
    }
}
