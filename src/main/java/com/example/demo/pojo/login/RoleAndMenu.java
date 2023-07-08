package com.example.demo.pojo.login;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;

@Data
public class RoleAndMenu {

    private int id;
    private int parentId;
    // 菜单名称
    private String name;
    // 用户角色
    private String role;
    // 路由展示的路径
    private String path;
    private JSONObject meta;
    // 可以访问到的路由
    private ArrayList<RoleAndMenu> children;
}
