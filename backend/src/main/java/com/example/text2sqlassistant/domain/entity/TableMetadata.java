package com.example.text2sqlassistant.domain.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TableMetadata {

    private Long id;
    private Long datasourceId;
    private String tableName;
    private String tableComment;
    private String bizName;
    private String bizDescription;
    private Integer isEnabled;
    private String schemaVersion;
    private LocalDateTime updateTime;
}
