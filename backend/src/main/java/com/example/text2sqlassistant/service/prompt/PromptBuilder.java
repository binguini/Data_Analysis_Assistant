package com.example.text2sqlassistant.service.prompt;

import com.example.text2sqlassistant.domain.dto.Nl2SqlRequest;

public interface PromptBuilder {

    String buildSqlPrompt(Nl2SqlRequest request);

    String buildExplainPrompt(String question, String sql);
}
