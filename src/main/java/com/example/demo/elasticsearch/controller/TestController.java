package com.example.demo.elasticsearch.controller;

import com.example.demo.elasticsearch.pojo.Entity;
import com.example.demo.elasticsearch.service.TestService;

import com.example.demo.pojo.result.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/elasticsearch")
public class TestController {

    @Autowired
    TestService testServiceImpl;

    @RequestMapping("/test")
    public ResultVO<List<Entity>> save(@RequestBody Entity entity) {

        testServiceImpl.save(entity);

        return testServiceImpl.findList(entity, 1, 1);
    }
}
