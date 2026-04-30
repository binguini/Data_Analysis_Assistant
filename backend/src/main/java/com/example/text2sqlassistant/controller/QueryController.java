package com.example.text2sqlassistant.controller;

import com.example.text2sqlassistant.common.ApiResponse;
import com.example.text2sqlassistant.domain.dto.Nl2SqlRequest;
import com.example.text2sqlassistant.domain.vo.Nl2SqlResponse;
import com.example.text2sqlassistant.service.Nl2SqlQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/query")
@RequiredArgsConstructor
public class QueryController {

    private final Nl2SqlQueryService nl2SqlQueryService;

    @PostMapping("/nl2sql")
    public ApiResponse<Nl2SqlResponse> nl2sql(@Valid @RequestBody Nl2SqlRequest request) {
        return ApiResponse.success(nl2SqlQueryService.query(request));
    }
}
