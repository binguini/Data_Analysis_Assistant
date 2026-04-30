package com.example.text2sqlassistant.mapper;

import com.example.text2sqlassistant.domain.entity.DatasourceConfig;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DatasourceMapper {

    List<DatasourceConfig> selectEnabledList();
}
