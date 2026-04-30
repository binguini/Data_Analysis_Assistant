package com.example.text2sqlassistant.domain.entity;

import lombok.Data;

@Data
public class ColumnMetadata {

    private Long id;
    private Long tableId;
    private String columnName;
    private String columnType;
    private String columnComment;
    private String bizName;
    private String bizDescription;
    private String exampleValue;
    private String enumDesc;
    private Integer isSensitive;
    private String maskType;
    private Integer isQueryable;
    private Integer isFilterable;
    private Integer isAggregatable;
}
