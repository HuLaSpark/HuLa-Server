package com.hula.ai.gpt.service.impl;

import com.hula.ai.common.utils.DateUtil;
import com.hula.ai.gpt.mapper.StatisticsMapper;
import com.hula.ai.gpt.pojo.param.StatisticsParam;
import com.hula.ai.gpt.service.IStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 统计接口实现类
 *
 * @author: 云裂痕
 * @date: 2023/1/13
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Service
public class StatisticsServiceImpl implements IStatisticsService {
    @Autowired
    private StatisticsMapper statisticsMapper;

    @Override
    public Map<String, Object> getTotalData() {
        return statisticsMapper.getTotalData();
    }

    @Override
    public List<Map<String, Object>> getLineData(StatisticsParam param) {
        return statisticsMapper.getLineData(param);
    }

    @Override
    public List<List<Object>> getRaddarData(StatisticsParam param) {
        List<Map<String, Object>> lists = statisticsMapper.getRaddarData(param);
        List<List<Object>> result = new ArrayList<>();
        lists.stream().forEach(v -> {
            List<Object> object = new ArrayList<>();
            String date = String.valueOf(v.get("date"));
            object.add(DateUtil.getCurrentWeek(DateUtil.parseLocalDate(date).getDayOfWeek().getValue()));
            object.add(v.get("chatCount"));
            object.add(v.get("drawCount"));
            result.add(object);
        });
        return result;
    }

    @Override
    public Map<String, Object> getPieData(StatisticsParam param) {
        return statisticsMapper.getPieData(param);
    }

    @Override
    public List<Map<String, Object>> getBarData(StatisticsParam param) {
        return statisticsMapper.getBarData(param);
    }

}
