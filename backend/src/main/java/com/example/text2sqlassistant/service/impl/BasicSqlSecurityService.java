package com.example.text2sqlassistant.service.impl;

import com.example.text2sqlassistant.service.security.SqlSecurityService;
import java.util.List;
import java.util.Locale;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BasicSqlSecurityService implements SqlSecurityService {

    private static final List<String> BLOCKED_KEYWORDS = List.of(
            "insert", "update", "delete", "drop", "alter", "truncate", "create", "replace", "grant", "revoke",
            "call", "load", "outfile", "infile"
    );

    private static final List<String> BLOCKED_FUNCTIONS = List.of(
            "load_file", "sleep", "benchmark", "sys_exec", "sys_eval", "extractvalue", "updatexml", "get_lock"
    );

    @Override
    public void validateReadOnly(String sql) {
        if (!StringUtils.hasText(sql)) {
            throw new IllegalArgumentException("SQL 不能为空");
        }
        String trimmedSql = stripTrailingSemicolon(sql.trim());
        rejectMultiStatement(trimmedSql);
        rejectBlockedKeywords(trimmedSql);

        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(trimmedSql);
        } catch (JSQLParserException exception) {
            throw new IllegalArgumentException("SQL 解析失败，已拒绝执行：" + exception.getMessage());
        }
        if (!(statement instanceof Select select)) {
            throw new IllegalArgumentException("仅允许执行只读 SELECT 查询");
        }
        rejectDangerousFunctions(select);
    }

    @Override
    public String appendLimitIfNecessary(String sql, int maxRows) {
        String trimmedSql = stripTrailingSemicolon(sql.trim());
        try {
            Statement statement = CCJSqlParserUtil.parse(trimmedSql);
            if (!(statement instanceof Select select)) {
                throw new IllegalArgumentException("仅允许为 SELECT 查询追加 LIMIT");
            }
            if (select.getSelectBody() instanceof PlainSelect plainSelect) {
                Limit limit = plainSelect.getLimit();
                if (limit == null) {
                    return trimmedSql + " LIMIT " + maxRows;
                }
                Long rowCount = limit.getRowCount() == null ? null : Long.valueOf(limit.getRowCount().toString());
                if (rowCount == null || rowCount > maxRows) {
                    limit.setRowCount(new Column(String.valueOf(maxRows)));
                }
                return select.toString();
            }
        } catch (JSQLParserException | NumberFormatException exception) {
            throw new IllegalArgumentException("SQL LIMIT 校验失败，已拒绝执行：" + exception.getMessage());
        }
        String normalized = trimmedSql.toLowerCase(Locale.ROOT);
        if (normalized.matches("(?s).*\\blimit\\s+\\d+.*")) {
            return trimmedSql;
        }
        return trimmedSql + " LIMIT " + maxRows;
    }

    private String stripTrailingSemicolon(String sql) {
        String trimmed = sql.trim();
        if (trimmed.endsWith(";")) {
            return trimmed.substring(0, trimmed.length() - 1).trim();
        }
        return trimmed;
    }

    private void rejectMultiStatement(String sql) {
        if (sql.contains(";")) {
            throw new IllegalArgumentException("禁止多语句执行");
        }
    }

    private void rejectBlockedKeywords(String sql) {
        String normalized = sql.toLowerCase(Locale.ROOT);
        for (String keyword : BLOCKED_KEYWORDS) {
            if (normalized.matches("(?s).*\\b" + keyword + "\\b.*")) {
                throw new IllegalArgumentException("检测到危险 SQL 关键字：" + keyword);
            }
        }
    }

    private void rejectDangerousFunctions(Select select) {
        ExpressionDeParser expressionDeParser = new ExpressionDeParser() {
            @Override
            public void visit(Function function) {
                String functionName = function.getName();
                if (functionName != null && BLOCKED_FUNCTIONS.contains(functionName.toLowerCase(Locale.ROOT))) {
                    throw new IllegalArgumentException("检测到危险 SQL 函数：" + functionName);
                }
                super.visit(function);
            }
        };
        select.accept(expressionDeParser);
    }
}
