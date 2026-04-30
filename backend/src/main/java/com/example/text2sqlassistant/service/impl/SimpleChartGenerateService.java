package com.example.text2sqlassistant.service.impl;

import com.example.text2sqlassistant.service.chart.ChartGenerateService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SimpleChartGenerateService implements ChartGenerateService {

    @Override
    public Map<String, Object> buildChartOption(String chartType, List<Map<String, Object>> rows) {
        Map<String, Object> option = new HashMap<>();
        option.put("title", Map.of("text", "查询结果图表"));
        option.put("tooltip", new HashMap<>());
        option.put("dataset", Map.of("source", rows));
        option.put("xAxis", Map.of("type", "category"));
        option.put("yAxis", Map.of("type", "value"));
        option.put("series", List.of(Map.of("type", chartType == null || chartType.isBlank() ? "line" : chartType)));
        return option;
    }
}
