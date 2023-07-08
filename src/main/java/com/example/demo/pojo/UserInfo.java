package com.example.demo.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_info")
public class UserInfo {
    // 用户id
    private int id;
    private String account;
    private String password;
    private String email;
    // 角色
    private String role;
    @TableField(value = "role_id")
    private String roleId;
    // 是否可以登录, 1可以，0不可以
    private int status;
    @TableField(exist = false)
    private String token;

}