package com.example.text2sqlassistant.domain.vo;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class Nl2SqlResponse {

    private String queryNo;
    private Boolean needClarify;
    private String clarifyQuestion;
    private String sql;
    private List<ColumnVO> columns;
    private List<Map<String, Object>> rows;
    private String explanation;
    private Map<String, Object> chartOption;
}
