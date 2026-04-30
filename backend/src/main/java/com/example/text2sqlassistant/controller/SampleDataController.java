package com.example.text2sqlassistant.controller;

import com.example.text2sqlassistant.common.ApiResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample")
@RequiredArgsConstructor
public class SampleDataController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_info", Integer.class);
        List<Map<String, Object>> latestRows = jdbcTemplate.queryForList(
                "SELECT user_id, register_time, channel, region FROM user_info ORDER BY register_time DESC LIMIT 5"
        );
        return ApiResponse.success(Map.of(
                "sampleTable", "user_info",
                "userCount", userCount == null ? 0 : userCount,
                "latestRows", latestRows
        ));
    }
}
