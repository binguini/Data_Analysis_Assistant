package com.example.text2sqlassistant.service.impl;

import com.example.text2sqlassistant.config.Text2SqlProperties;
import com.example.text2sqlassistant.service.executor.QueryExecuteService;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcQueryExecuteService implements QueryExecuteService {

    private final JdbcTemplate jdbcTemplate;
    private final Text2SqlProperties properties;

    @Override
    public List<Map<String, Object>> execute(Long datasourceId, String sql) {
        return jdbcTemplate.query(sql, statement -> configureStatement(statement), (resultSet, rowNum) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String label = resultSet.getMetaData().getColumnLabel(i);
                row.put(label, resultSet.getObject(i));
            }
            return row;
        });
    }

    private void configureStatement(Statement statement) {
        try {
            statement.setQueryTimeout(properties.getQuery().getTimeoutSeconds());
            statement.setMaxRows(properties.getQuery().getMaxRows());
        } catch (Exception exception) {
            throw new IllegalStateException("配置 SQL 查询参数失败", exception);
        }
    }
}
