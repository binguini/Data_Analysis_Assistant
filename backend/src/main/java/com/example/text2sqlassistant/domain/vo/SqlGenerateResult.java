package com.example.text2sqlassistant.domain.vo;

import java.util.List;
import lombok.Data;

@Data
public class SqlGenerateResult {

    private boolean needClarify;
    private String clarifyQuestion;
    private String sql;
    private String thoughtSummary;
    private List<String> usedTables;
    private List<String> usedFields;
    private ChartSuggestion chartSuggestion;
    private List<String> riskTips;

    @Data
    public static class ChartSuggestion {
        private String chartType;
        private String dimensionField;
        private List<String> metricFields;
    }
}
