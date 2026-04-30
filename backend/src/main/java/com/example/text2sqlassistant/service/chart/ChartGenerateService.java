package com.example.text2sqlassistant.service.chart;

import java.util.List;
import java.util.Map;

public interface ChartGenerateService {

    Map<String, Object> buildChartOption(String chartType, List<Map<String, Object>> rows);
}
