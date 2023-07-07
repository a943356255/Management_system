package com.example.demo.elasticsearch.pojo;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
@Document(indexName = "entity")
public class Entity implements Serializable {

    @Id
    private int id;

    @Field(type = FieldType.Text)
    private String name;

    // , analyzer = "ik_smart", searchAnalyzer = "ik_max_word"
    // String 类型的可以被写成FieldType.Text 或者FieldType.Keyword
    // 区别：text field values are analyzed for full-text search
    // while keyword strings are left as-is for filtering and sorting.
    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type = FieldType.Keyword)
    private String password;

    private Map<String, List<String>> mapAndList;
}
