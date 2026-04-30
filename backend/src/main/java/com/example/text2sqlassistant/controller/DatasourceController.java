package com.example.text2sqlassistant.controller;

import com.example.text2sqlassistant.common.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/datasource")
public class DatasourceController {

    @GetMapping("/page")
    public ApiResponse<List<Map<String, Object>>> page() {
        return ApiResponse.success(List.of(Map.of("id", 1, "name", "示例 MySQL 数据源", "bizDomain", "user")));
    }

    @PostMapping("/{id}/sync-schema")
    public ApiResponse<String> syncSchema() {
        return ApiResponse.success("Schema 同步任务已提交");
    }
}
