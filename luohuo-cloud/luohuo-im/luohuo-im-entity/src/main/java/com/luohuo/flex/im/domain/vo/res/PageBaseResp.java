package com.luohuo.flex.im.domain.vo.res;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础翻页返回
 * @author nyh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "基础翻页返回")
public class PageBaseResp<T> {

    @Schema(description = "当前页数")
    private Integer pageNo;

    @Schema(description = "每页查询数量")
    private Integer pageSize;

    @Schema(description = "总记录数")
    private Long totalRecords;

    @Schema(description = "是否最后一页")
    private Boolean isLast = Boolean.FALSE;

    @Schema(description = "数据列表")
    private List<T> list;


    public static <T> PageBaseResp<T> empty() {
        PageBaseResp<T> r = new PageBaseResp<>();
        r.setPageNo(1);
        r.setPageSize(0);
        r.setIsLast(true);
        r.setTotalRecords(0L);
        r.setList(new ArrayList<>());
        return r;
    }

    public static <T> PageBaseResp<T> init(Integer pageNo, Integer pageSize, Long totalRecords, Boolean isLast, List<T> list) {
        return new PageBaseResp<>(pageNo, pageSize, totalRecords, isLast, list);
    }

    public static <T> PageBaseResp<T> init(Integer pageNo, Integer pageSize, Long totalRecords, List<T> list) {
        return new PageBaseResp<>(pageNo, pageSize, totalRecords, isLastPage(totalRecords, pageNo, pageSize), list);
    }

    public static <T> PageBaseResp<T> init(IPage<T> page) {
        return init((int) page.getCurrent(), (int) page.getSize(), page.getTotal(), page.getRecords());
    }

    public static <T> PageBaseResp<T> init(IPage page, List<T> list) {
        return init((int) page.getCurrent(), (int) page.getSize(), page.getTotal(), list);
    }

    public static <T> PageBaseResp<T> init(PageBaseResp resp, List<T> list) {
        return init(resp.getPageNo(), resp.getPageSize(), resp.getTotalRecords(), resp.getIsLast(), list);
    }

    /**
     * 是否是最后一页
     */
    public static Boolean isLastPage(long totalRecords, int pageNo, int pageSize) {
        if (pageSize == 0) {
            return false;
        }
        long pageTotal = totalRecords / pageSize + (totalRecords % pageSize == 0 ? 0 : 1);
        return pageNo >= pageTotal ? true : false;
    }
}
