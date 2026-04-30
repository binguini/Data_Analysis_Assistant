package com.example.text2sqlassistant.service.impl;

import com.example.text2sqlassistant.domain.dto.Nl2SqlRequest;
import com.example.text2sqlassistant.service.prompt.PromptBuilder;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class DefaultPromptBuilder implements PromptBuilder {

    @Override
    public String buildSqlPrompt(Nl2SqlRequest request) {
        return """
                你是企业级数据分析 Text-to-SQL 助手，请根据用户问题生成 MySQL 查询语句。

                必须遵守以下规则：
                1. 只能生成 SELECT 查询。
                2. 禁止 INSERT、UPDATE、DELETE、DROP、ALTER、TRUNCATE、CREATE、GRANT、CALL 等写操作或管理操作。
                3. SQL 不允许包含分号，不允许多语句执行。
                4. 如果问题信息不足，请返回 needClarify=true，并给出 clarifyQuestion，不要编造 SQL。
                5. 输出必须是 JSON，不要输出 Markdown。

                当前日期：%s
                数据库类型：MySQL
                业务域：%s

                MVP 阶段可用示例 Schema：
                表：user_info，用户信息表
                字段：
                - user_id BIGINT，用户唯一 ID
                - register_time DATETIME，注册时间
                - channel VARCHAR，注册渠道
                - region VARCHAR，地区

                用户问题：%s

                请返回如下 JSON：
                {
                  "needClarify": false,
                  "clarifyQuestion": "",
                  "sql": "",
                  "thoughtSummary": "简要说明 SQL 生成依据",
                  "usedTables": [],
                  "usedFields": [],
                  "chartSuggestion": {
                    "chartType": "line|bar|pie|table",
                    "dimensionField": "",
                    "metricFields": []
                  },
                  "riskTips": []
                }
                """.formatted(LocalDate.now(), nullToBlank(request.getBizDomain()), request.getQuestion());
    }

    @Override
    public String buildExplainPrompt(String question, String sql) {
        return "请根据用户问题和 SQL 生成简洁结果说明。问题：" + question + "；SQL：" + sql;
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}
