package com.hula.ai.controller.app;

import cn.hutool.core.util.ObjectUtil;
import com.hula.ai.common.utils.DateUtil;
import com.hula.ai.gpt.pojo.param.StatisticsParam;
import com.hula.ai.gpt.service.IStatisticsService;
import com.hula.domain.vo.res.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 统计接口
 *
 * @author: 云裂痕
 * @date: 2025/03/07
 * @version: 1.2.8
 * 得其道 乾乾
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    @Autowired
    private IStatisticsService statisticsService;

    /**
     * 获取总数据
     *
     * @author: 云裂痕
	 * @date: 2025/03/07
	 * @version: 1.2.8
     */
    @GetMapping("/index/total")
    public ApiResult getTotalData() {
        return ApiResult.success(statisticsService.getTotalData());
    }

    /**
     * 获取折线图数据
     *
     * @author: 云裂痕
	 * @date: 2025/03/07
	 * @version: 1.2.8
     */
    @GetMapping("/index/line")
    public ApiResult getLineData(@RequestParam StatisticsParam param) {
        if (ObjectUtil.isNull(param.getStartDate()) && ObjectUtil.isNull(param.getEndDate())) {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = LocalDate.now().plusWeeks(-1);
			param.setStartDate(DateUtil.formatLocalDate(startDate));
			param.setEndDate(DateUtil.formatLocalDate(endDate));
        }
        return ApiResult.success(statisticsService.getLineData(param));
    }

    /**
     * 获取雷达图数据
     *
     * @author: 云裂痕
	 * @date: 2025/03/07
	 * @version: 1.2.8
     */
    @GetMapping("/index/raddar")
    public ApiResult getRaddarData(@RequestParam StatisticsParam param) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().plusWeeks(-1);

		param.setStartDate(DateUtil.formatLocalDate(startDate));
		param.setEndDate(DateUtil.formatLocalDate(endDate));
        return ApiResult.success(statisticsService.getRaddarData(param));
    }

    /**
     * 获取饼图数据
     *
     * @author: 云裂痕
	 * @date: 2025/03/07
	 * @version: 1.2.8
     */
    @GetMapping("/index/pie")
    public ApiResult getPieData(@RequestParam StatisticsParam param) {
        return ApiResult.success(statisticsService.getPieData(param));
    }

    /**
     * 获取柱状图图数据
     *
     * @author: 云裂痕
	 * @date: 2025/03/07
	 * @version: 1.2.8
     */
    @GetMapping("/index/bar")
    public ApiResult getBarData(@RequestParam StatisticsParam param) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.now().plusWeeks(-1);
		param.setStartDate(DateUtil.formatLocalDate(startDate));
		param.setEndDate(DateUtil.formatLocalDate(endDate));
        return ApiResult.success(statisticsService.getBarData(param));
    }

}
