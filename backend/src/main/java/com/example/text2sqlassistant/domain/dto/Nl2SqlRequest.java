package com.example.text2sqlassistant.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Nl2SqlRequest {

    @NotNull(message = "数据源 ID 不能为空")
    private Long datasourceId;

    private String bizDomain;

    @NotBlank(message = "问题不能为空")
    private String question;

    private String chartType;
}
