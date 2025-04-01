package com.hula.ai.gpt.mapper;

import com.hula.ai.gpt.pojo.param.StatisticsParam;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 统计接口 数据层
 *
 * @author: 云裂痕
 * @date: 2025/03/08
 * @version: 1.2.8
 * 得其道 乾乾
 */
public interface StatisticsMapper {

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
    List<Map<String, Object>> getLineData(@Param("q") StatisticsParam param);

    /**
     * 获取雷达图数据
     *
     * @param param 查询条件
     * @return
     */
    List<Map<String, Object>> getRaddarData(@Param("q") StatisticsParam param);

    /**
     * 获取饼图数据
     *
     * @param param 查询条件
     * @return
     */
    Map<String, Object> getPieData(@Param("q") StatisticsParam param);

    /**
     * 获取柱状图总数据
     *
     * @param param 查询条件
     * @return
     */
    List<Map<String, Object>> getBarData(@Param("q") StatisticsParam param);

}
