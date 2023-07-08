package com.example.demo.pojo.file;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("file")
public class FileInfo {

    private int id;

    private String filename;

    private String type;

    @TableField(value = "path")
    private String location;

    private String time;

    private String actor;

}
