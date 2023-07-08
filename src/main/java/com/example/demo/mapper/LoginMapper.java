package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.pojo.UserInfo;
import com.example.demo.pojo.login.AsyncRouterMap;
import com.example.demo.pojo.login.RoleAndMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginMapper extends BaseMapper<UserInfo> {

    List<RoleAndMenu> getRoleAndMenu(@Param("roleId") int roleId);

    List<AsyncRouterMap> getRouter();
}
