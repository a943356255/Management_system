package com.example.demo.service.file;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.pojo.file.FileInfo;

public interface ChunkService extends IService<FileInfo> {

    void insert(String filename, String filePath, String type, String actor);

}
