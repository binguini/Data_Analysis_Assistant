package com.example.text2sqlassistant.domain.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TableRelation {

    private Long id;
    private Long datasourceId;
    private String sourceTable;
    private String sourceColumn;
    private String targetTable;
    private String targetColumn;
    private String relationType;
    private String description;
    private LocalDateTime updateTime;
}
