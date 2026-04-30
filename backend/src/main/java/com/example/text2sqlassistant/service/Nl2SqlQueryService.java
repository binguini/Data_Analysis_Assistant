package com.example.text2sqlassistant.service;

import com.example.text2sqlassistant.domain.dto.Nl2SqlRequest;
import com.example.text2sqlassistant.domain.vo.Nl2SqlResponse;

public interface Nl2SqlQueryService {

    Nl2SqlResponse query(Nl2SqlRequest request);
}
