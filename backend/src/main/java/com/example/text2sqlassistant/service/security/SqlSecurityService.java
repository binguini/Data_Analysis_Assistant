package com.example.text2sqlassistant.service.security;

public interface SqlSecurityService {

    void validateReadOnly(String sql);

    String appendLimitIfNecessary(String sql, int maxRows);
}
