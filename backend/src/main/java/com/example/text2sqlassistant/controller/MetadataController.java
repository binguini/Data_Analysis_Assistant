package com.example.text2sqlassistant.controller;

import com.example.text2sqlassistant.common.ApiResponse;
import com.example.text2sqlassistant.domain.entity.ColumnMetadata;
import com.example.text2sqlassistant.domain.entity.TableMetadata;
import com.example.text2sqlassistant.domain.entity.TableRelation;
import com.example.text2sqlassistant.mapper.MetadataMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metadata")
@RequiredArgsConstructor
public class MetadataController {

    private final MetadataMapper metadataMapper;

    @GetMapping("/tables")
    public ApiResponse<List<TableMetadata>> listTables(@RequestParam Long datasourceId) {
        return ApiResponse.success(metadataMapper.selectTables(datasourceId));
    }

    @GetMapping("/tables/{tableId}/columns")
    public ApiResponse<List<ColumnMetadata>> listColumns(@PathVariable Long tableId) {
        return ApiResponse.success(metadataMapper.selectColumns(tableId));
    }

    @GetMapping("/relations")
    public ApiResponse<List<TableRelation>> listRelations(@RequestParam Long datasourceId) {
        return ApiResponse.success(metadataMapper.selectRelations(datasourceId));
    }

    @PutMapping("/tables/{id}")
    public ApiResponse<Integer> updateTable(@PathVariable Long id, @RequestBody TableMetadata tableMetadata) {
        tableMetadata.setId(id);
        return ApiResponse.success(metadataMapper.updateTable(tableMetadata));
    }

    @PutMapping("/columns/{id}")
    public ApiResponse<Integer> updateColumn(@PathVariable Long id, @RequestBody ColumnMetadata columnMetadata) {
        columnMetadata.setId(id);
        return ApiResponse.success(metadataMapper.updateColumn(columnMetadata));
    }

    @PostMapping("/relations")
    public ApiResponse<TableRelation> createRelation(@RequestBody TableRelation tableRelation) {
        metadataMapper.insertRelation(tableRelation);
        return ApiResponse.success(tableRelation);
    }

    @DeleteMapping("/relations/{id}")
    public ApiResponse<Integer> deleteRelation(@PathVariable Long id) {
        return ApiResponse.success(metadataMapper.deleteRelation(id));
    }
}
