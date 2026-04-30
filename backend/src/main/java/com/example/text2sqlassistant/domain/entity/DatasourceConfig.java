package com.example.text2sqlassistant.domain.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DatasourceConfig {

    private Long id;
    private String name;
    private String dbType;
    private String host;
    private Integer port;
    private String databaseName;
    private String username;
    private String passwordEncrypt;
    private String bizDomain;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
