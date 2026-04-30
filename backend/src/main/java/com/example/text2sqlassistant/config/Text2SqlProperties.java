package com.example.text2sqlassistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "text2sql")
public class Text2SqlProperties {

    private Llm llm = new Llm();
    private Query query = new Query();

    @Data
    public static class Llm {
        private String provider;
        private String baseUrl;
        private String apiKey;
        private String model;
        private int timeoutSeconds = 30;
        private boolean mockEnabled = true;
    }

    @Data
    public static class Query {
        private int maxRows = 1000;
        private int timeoutSeconds = 15;
    }
}
