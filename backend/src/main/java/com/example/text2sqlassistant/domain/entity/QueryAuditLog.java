package com.example.text2sqlassistant.domain.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class QueryAuditLog {

    private Long id;
    private String queryNo;
    private Long userId;
    private Long datasourceId;
    private String question;
    private String promptVersion;
    private String modelName;
    private String modelOutput;
    private String generatedSql;
    private String executedSql;
    private String status;
    private String errorMessage;
    private Integer durationMs;
    private Integer rowCount;
    private String chartType;
    private String resultSummary;
    private LocalDateTime createTime;
}
