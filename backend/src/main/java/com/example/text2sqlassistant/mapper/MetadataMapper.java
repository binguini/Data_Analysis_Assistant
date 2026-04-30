package com.example.text2sqlassistant.mapper;

import com.example.text2sqlassistant.domain.entity.ColumnMetadata;
import com.example.text2sqlassistant.domain.entity.TableMetadata;
import com.example.text2sqlassistant.domain.entity.TableRelation;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MetadataMapper {

    List<TableMetadata> selectTables(@Param("datasourceId") Long datasourceId);

    List<ColumnMetadata> selectColumns(@Param("tableId") Long tableId);

    List<TableRelation> selectRelations(@Param("datasourceId") Long datasourceId);

    int updateTable(TableMetadata tableMetadata);

    int updateColumn(ColumnMetadata columnMetadata);

    int insertRelation(TableRelation tableRelation);

    int deleteRelation(@Param("id") Long id);
}
