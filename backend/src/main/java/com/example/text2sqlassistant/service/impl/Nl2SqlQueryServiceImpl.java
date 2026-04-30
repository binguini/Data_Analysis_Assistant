package com.example.text2sqlassistant.service.impl;

import com.example.text2sqlassistant.config.Text2SqlProperties;
import com.example.text2sqlassistant.domain.dto.Nl2SqlRequest;
import com.example.text2sqlassistant.domain.entity.QueryAuditLog;
import com.example.text2sqlassistant.domain.vo.ColumnVO;
import com.example.text2sqlassistant.domain.vo.Nl2SqlResponse;
import com.example.text2sqlassistant.domain.vo.SqlGenerateResult;
import com.example.text2sqlassistant.mapper.QueryAuditLogMapper;
import com.example.text2sqlassistant.service.Nl2SqlQueryService;
import com.example.text2sqlassistant.service.chart.ChartGenerateService;
import com.example.text2sqlassistant.service.executor.QueryExecuteService;
import com.example.text2sqlassistant.service.llm.LlmClient;
import com.example.text2sqlassistant.service.prompt.PromptBuilder;
import com.example.text2sqlassistant.service.security.SqlSecurityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class Nl2SqlQueryServiceImpl implements Nl2SqlQueryService {

    private final PromptBuilder promptBuilder;
    private final LlmClient llmClient;
    private final SqlSecurityService sqlSecurityService;
    private final QueryExecuteService queryExecuteService;
    private final ChartGenerateService chartGenerateService;
    private final Text2SqlProperties properties;
    private final ObjectMapper objectMapper;
    private final QueryAuditLogMapper queryAuditLogMapper;

    @Override
    public Nl2SqlResponse query(Nl2SqlRequest request) {
        long startedAt = System.currentTimeMillis();
        String queryNo = buildQueryNo();
        String prompt = null;
        String modelOutput = null;
        SqlGenerateResult generateResult = null;
        String executableSql = null;
        List<Map<String, Object>> rows = List.of();
        try {
            prompt = promptBuilder.buildSqlPrompt(request);
            modelOutput = llmClient.generate(prompt);
            generateResult = parseModelOutput(modelOutput);

            Nl2SqlResponse response = new Nl2SqlResponse();
            response.setQueryNo(queryNo);
            response.setNeedClarify(generateResult.isNeedClarify());
            response.setClarifyQuestion(generateResult.getClarifyQuestion());

            if (generateResult.isNeedClarify()) {
                response.setColumns(List.of());
                response.setRows(List.of());
                response.setChartOption(Map.of());
                saveAuditLog(request, queryNo, modelOutput, generateResult, null, rows, "CLARIFY", null, startedAt);
                return response;
            }

            String sql = generateResult.getSql();
            sqlSecurityService.validateReadOnly(sql);
            executableSql = sqlSecurityService.appendLimitIfNecessary(sql, properties.getQuery().getMaxRows());
            rows = queryExecuteService.execute(request.getDatasourceId(), executableSql);

            response.setSql(executableSql);
            response.setColumns(inferColumns(rows));
            response.setRows(rows);
            response.setExplanation(buildExplanation(request, generateResult, rows));
            response.setChartOption(chartGenerateService.buildChartOption(resolveChartType(request, generateResult), rows));
            saveAuditLog(request, queryNo, modelOutput, generateResult, executableSql, rows, "SUCCESS", null, startedAt);
            return response;
        } catch (RuntimeException exception) {
            saveAuditLog(request, queryNo, modelOutput, generateResult, executableSql, rows, "FAILED", exception.getMessage(), startedAt);
            throw exception;
        }
    }

    private SqlGenerateResult parseModelOutput(String modelOutput) {
        String json = unwrapJson(modelOutput);
        try {
            return objectMapper.readValue(json, SqlGenerateResult.class);
        } catch (Exception exception) {
            throw new IllegalArgumentException("模型输出不是合法 JSON：" + exception.getMessage());
        }
    }

    private String unwrapJson(String modelOutput) {
        String text = modelOutput == null ? "" : modelOutput.trim();
        if (text.startsWith("```")) {
            text = text.replaceFirst("^```[a-zA-Z]*", "").replaceFirst("```$", "").trim();
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end <= start) {
            throw new IllegalArgumentException("模型输出中未找到 JSON");
        }
        return text.substring(start, end + 1);
    }

    private List<ColumnVO> inferColumns(List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }
        List<ColumnVO> columns = new ArrayList<>();
        Map<String, Object> firstRow = rows.get(0);
        firstRow.forEach((key, value) -> columns.add(new ColumnVO(key, key, inferType(value))));
        return columns;
    }

    private String inferType(Object value) {
        if (value instanceof Number) {
            return "number";
        }
        return "string";
    }

    private String buildExplanation(Nl2SqlRequest request, SqlGenerateResult generateResult, List<Map<String, Object>> rows) {
        String summary = StringUtils.hasText(generateResult.getThoughtSummary())
                ? generateResult.getThoughtSummary()
                : "已根据问题生成 SQL 并返回查询结果。";
        return summary + " 本次问题为：“" + request.getQuestion() + "”，返回 " + rows.size() + " 行数据。";
    }

    private String resolveChartType(Nl2SqlRequest request, SqlGenerateResult generateResult) {
        if (StringUtils.hasText(request.getChartType())) {
            return request.getChartType();
        }
        if (generateResult != null && generateResult.getChartSuggestion() != null && StringUtils.hasText(generateResult.getChartSuggestion().getChartType())) {
            return generateResult.getChartSuggestion().getChartType();
        }
        return "line";
    }

    private void saveAuditLog(
            Nl2SqlRequest request,
            String queryNo,
            String modelOutput,
            SqlGenerateResult generateResult,
            String executableSql,
            List<Map<String, Object>> rows,
            String status,
            String errorMessage,
            long startedAt
    ) {
        try {
            QueryAuditLog auditLog = new QueryAuditLog();
            auditLog.setQueryNo(queryNo);
            auditLog.setDatasourceId(request.getDatasourceId());
            auditLog.setQuestion(request.getQuestion());
            auditLog.setPromptVersion("v1");
            auditLog.setModelName(properties.getLlm().getModel());
            auditLog.setModelOutput(modelOutput);
            auditLog.setGeneratedSql(generateResult == null ? null : generateResult.getSql());
            auditLog.setExecutedSql(executableSql);
            auditLog.setStatus(status);
            auditLog.setErrorMessage(errorMessage);
            auditLog.setDurationMs(Math.toIntExact(Math.min(System.currentTimeMillis() - startedAt, Integer.MAX_VALUE)));
            auditLog.setRowCount(rows == null ? 0 : rows.size());
            auditLog.setChartType(resolveChartType(request, generateResult));
            auditLog.setResultSummary(status + "，返回 " + (rows == null ? 0 : rows.size()) + " 行");
            queryAuditLogMapper.insert(auditLog);
        } catch (Exception ignored) {
            // 审计日志不能影响主查询链路。
        }
    }

    private String buildQueryNo() {
        return "Q" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
}
