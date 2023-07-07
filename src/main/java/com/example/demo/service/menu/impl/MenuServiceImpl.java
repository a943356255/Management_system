package com.example.demo.service.menu.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.mapper.crud.CRUDMapper;
import com.example.demo.service.menu.MenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    CRUDMapper crudMapper;

    @Override
    public int addMenu(Map<String, Object> map) {
        // 用于动态路由的渲染数据
        Map<String, Object> addMap = new HashMap<>();

        addMap.put("path", map.get("path"));
        addMap.put("name", map.get("name"));

        addMap.put("component", "../../views" + map.get("component"));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("icon", map.get("icon"));
        String[] arr = map.get("role").toString().split(",");
        jsonObject.put("role", arr);
        jsonObject.put("title", map.get("title"));

        addMap.put("meta", jsonObject);

        // 用于侧边栏的添加
        Map<String, Object> seeMap = new HashMap<>();
        seeMap.put("name", map.get("name"));
        seeMap.put("path", map.get("path"));
//        seeMap.put("parent_id", crudMapper.select());
        seeMap.put("meta", "");
        return crudMapper.insert(addMap, "async_router_map");
    }
}
