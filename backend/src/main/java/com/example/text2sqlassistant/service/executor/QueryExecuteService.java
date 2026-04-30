package com.example.text2sqlassistant.service.executor;

import java.util.List;
import java.util.Map;

public interface QueryExecuteService {

    List<Map<String, Object>> execute(Long datasourceId, String sql);
}
