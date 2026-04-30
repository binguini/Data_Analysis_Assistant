package com.example.text2sqlassistant.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnVO {

    private String name;
    private String label;
    private String type;
}
