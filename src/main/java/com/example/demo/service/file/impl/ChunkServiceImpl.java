package com.example.demo.service.file.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.controller.file_controller.FileController;
import com.example.demo.mapper.file.FileMapper;
import com.example.demo.pojo.file.FileInfo;
import com.example.demo.service.file.ChunkService;
import com.example.demo.utils.Utils;
import org.springframework.stereotype.Service;

@Service
public class ChunkServiceImpl extends ServiceImpl<FileMapper, FileInfo> implements ChunkService {

    @Override
    public void insert(String filename, String filePath, String type, String actor) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilename(filename.split("\\.")[0]);
        fileInfo.setLocation(filePath);
        fileInfo.setTime(Utils.getTime());
        fileInfo.setType(Utils.getType(filename));
        fileInfo.setType(type);

        if (type.equals("video")) {
            fileInfo.setActor(actor);
        }

        // 如果是pdf，则进行拆分
        if (type.equals("pdf")) {
            new FileController().getSinglePDF(filePath, filename);
        }

        save(fileInfo);
    }
}