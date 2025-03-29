package com.hula.ai.gpt.service;

import com.hula.ai.gpt.pojo.param.StatisticsParam;

import java.util.List;
import java.util.Map;

/**
 * 统计接口
 *
 * @author: 云裂痕
 * @date: 2023/1/13
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface IStatisticsService {

    /**
     * 获取首页总数据
     *
     * @return
     */
    Map<String, Object> getTotalData();

    /**
     * 获取折线图数据
     *
     * @param param 查询条件
     * @return
     */
    List<Map<String, Object>> getLineData(StatisticsParam param);

    /**
     * 获取雷达图数据
     *
     * @param param 查询条件
     * @return
     */
    List<List<Object>> getRaddarData(StatisticsParam param);

    /**
     * 获取饼图数据
     *
     * @param param 查询条件
     * @return
     */
    Map<String, Object> getPieData(StatisticsParam param);

    /**
     * 获取柱状图总数据
     *
     * @param param 查询条件
     * @return
     */
    List<Map<String, Object>> getBarData(StatisticsParam param);

}
