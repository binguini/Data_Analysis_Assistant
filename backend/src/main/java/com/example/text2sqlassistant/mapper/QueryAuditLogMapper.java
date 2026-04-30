package com.example.text2sqlassistant.mapper;

import com.example.text2sqlassistant.domain.entity.QueryAuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QueryAuditLogMapper {

    int insert(QueryAuditLog auditLog);
}
