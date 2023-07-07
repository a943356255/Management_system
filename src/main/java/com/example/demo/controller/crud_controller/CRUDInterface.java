package com.example.demo.controller.crud_controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.crud.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/crudInterface")
public class CRUDInterface {

    @Autowired
    CRUDService crudServiceImpl;

    @RequestMapping("/allCRUD")
    public JSONObject testInterface(@RequestBody Map<String, Object> map) {
        return crudServiceImpl.getCrudValue(map);
    }


}