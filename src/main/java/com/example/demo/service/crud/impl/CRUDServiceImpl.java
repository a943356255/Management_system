package com.example.demo.service.crud.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.mapper.crud.CRUDMapper;
import com.example.demo.service.crud.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional
public class CRUDServiceImpl implements CRUDService {

    @Autowired
    CRUDMapper crudMapper;

    @Override
    public JSONObject getCrudValue(Map<String, Object> map) {

        JSONObject jsonObject = new JSONObject();
        // 获取要操作的表
        String table = (String) map.get("table");
        // 要执行的操作
        String operate = (String) map.get("operate");
        System.out.println("本次操作是：" + operate);

        // 需要操作的字段
        LinkedHashMap<String, Object> columns = (LinkedHashMap) map.get("column");
        System.out.println("columns = " + columns);

        // 需要排序的字段
        // select * from table_name order by column_1, column_2 desc

        // key为要限制的字段，value为具体的限制结果
        LinkedHashMap<String, Object> condition = (LinkedHashMap) map.get("condition");

        // 如果不存在字段，直接返回
        if (columns == null) {
            jsonObject.put("code", -1);
            return jsonObject;
        }

        switch (operate) {
            case "add":
                // 添加数据
                crudMapper.insert(columns, table);
                break;
            case "select":
                int pageNo = (int) map.get("pageNo");
                int pageSize = (int) map.get("pageSize");
                int excelPageSize = 0;
                if (map.get("excelPageSize") != null) {
                    excelPageSize = (int) map.get("excelPageSize");
                }

                LinkedHashMap<String, Object> like = (LinkedHashMap<String, Object>) map.get("like");
                System.out.println("like = " + like);

                String searchValue = (String) map.get("searchValue");

                ArrayList<Map<String, Object>> list;

                // 判断是否是因为导出excel需要查询数据
                if (excelPageSize != 0) {
                    list = crudMapper.select(columns, condition, table, (pageNo - 1) * pageSize, excelPageSize, like, searchValue);
                } else {
                    list = crudMapper.select(columns, condition, table, (pageNo - 1) * pageSize, pageSize, like, searchValue);
                }

                System.out.println("查询结果大小为:" + list.size());
                jsonObject.put("valueList", list);

                int size = crudMapper.selectCount(columns, condition, table, like);
                System.out.println("size大小为" + size);
                jsonObject.put("size", size);

                break;
            case "delete":
                // 删除操作,如果不传条件，默认删除失败
                if (condition == null) {
                    jsonObject.put("code", -1);
                    return jsonObject;
                }
                crudMapper.delete(table, condition);
                break;
            case "update":
                // 修改操作
                crudMapper.update(columns, table, condition);
                break;
            default:
                // 不是上述四种操作，直接返回结果
                jsonObject.put("code", -1);
                return jsonObject;
        }

        return jsonObject;
    }

}
