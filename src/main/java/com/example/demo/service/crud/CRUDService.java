package com.example.demo.service.crud;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface CRUDService {

    JSONObject getCrudValue(Map<String, Object> map);
}
