package com.example.text2sqlassistant.service.llm;

import com.example.text2sqlassistant.config.Text2SqlProperties;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class DeepSeekLlmClient implements LlmClient {

    private final Text2SqlProperties properties;

    @Override
    public String generate(String prompt) {
        if (properties.getLlm().isMockEnabled() || !StringUtils.hasText(properties.getLlm().getApiKey())) {
            return mockGenerate();
        }

        WebClient client = WebClient.builder()
                .baseUrl(properties.getLlm().getBaseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getLlm().getApiKey())
                .build();

        Map<String, Object> request = Map.of(
                "model", properties.getLlm().getModel(),
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.1
        );

        Map<?, ?> response = client.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block(Duration.ofSeconds(properties.getLlm().getTimeoutSeconds()));

        return extractContent(response);
    }

    private String extractContent(Map<?, ?> response) {
        if (response == null) {
            throw new IllegalStateException("模型响应为空");
        }
        Object choicesObject = response.get("choices");
        if (!(choicesObject instanceof List<?> choices) || choices.isEmpty()) {
            throw new IllegalStateException("模型响应缺少 choices");
        }
        Object firstChoice = choices.get(0);
        if (!(firstChoice instanceof Map<?, ?> choice)) {
            throw new IllegalStateException("模型响应格式不正确");
        }
        Object messageObject = choice.get("message");
        if (!(messageObject instanceof Map<?, ?> message)) {
            throw new IllegalStateException("模型响应缺少 message");
        }
        Object content = message.get("content");
        if (!(content instanceof String text) || !StringUtils.hasText(text)) {
            throw new IllegalStateException("模型响应内容为空");
        }
        return text;
    }

    private String mockGenerate() {
        return "{\"needClarify\":false,\"clarifyQuestion\":\"\",\"sql\":\"SELECT DATE(register_time) AS stat_date, COUNT(DISTINCT user_id) AS new_user_count FROM user_info WHERE register_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) GROUP BY DATE(register_time) ORDER BY stat_date\",\"thoughtSummary\":\"根据新增用户趋势场景生成按天聚合 SQL\",\"usedTables\":[\"user_info\"],\"usedFields\":[\"register_time\",\"user_id\"],\"chartSuggestion\":{\"chartType\":\"line\",\"dimensionField\":\"stat_date\",\"metricFields\":[\"new_user_count\"]},\"riskTips\":[\"当前为 MVP 示例结果\"]}";
    }
}
