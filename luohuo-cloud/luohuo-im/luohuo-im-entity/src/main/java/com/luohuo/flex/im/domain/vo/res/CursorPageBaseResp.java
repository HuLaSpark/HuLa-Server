package com.luohuo.flex.im.domain.vo.res;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


/**
 * 游标翻页返回
 * @author nyh
 */
@Data
@Schema(description = "游标翻页返回")
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseResp<T> {

    @Schema(description = "游标（下次翻页带上这参数）")
    private String cursor;

    @Schema(description = "是否最后一页")
    private Boolean isLast = Boolean.FALSE;

    @Schema(description = "数据列表")
    private List<T> list;

    @Schema(description = "总数")
    private Long total;

    public static <T> CursorPageBaseResp<T> init(CursorPageBaseResp cursorPage, List<T> list, Long total) {
        CursorPageBaseResp<T> cursorPageBaseResp = new CursorPageBaseResp<>();
        cursorPageBaseResp.setIsLast(cursorPage.getIsLast());
        cursorPageBaseResp.setList(list);
        cursorPageBaseResp.setCursor(cursorPage.getCursor());
        cursorPageBaseResp.setTotal(total);
        return cursorPageBaseResp;
    }

    @JsonIgnore
    public Boolean isEmpty() {
        return CollectionUtil.isEmpty(list);
    }

    public static <T> CursorPageBaseResp<T> empty() {
        CursorPageBaseResp<T> cursorPageBaseResp = new CursorPageBaseResp<>();
        cursorPageBaseResp.setIsLast(true);
        cursorPageBaseResp.setList(new ArrayList<>());
        cursorPageBaseResp.setTotal(0L);
        return cursorPageBaseResp;
    }

}
