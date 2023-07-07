package com.example.demo.service.login;

import com.example.demo.pojo.UserInfo;
import com.example.demo.pojo.login.AsyncRouterMap;
import com.example.demo.pojo.login.RoleAndMenu;
import com.example.demo.pojo.result.ResultVO;

import java.util.List;
import java.util.Map;

public interface LoginService {

    ResultVO<UserInfo> userLogin(Map<String, Object> map);

    ResultVO<List<RoleAndMenu>> getMenuByRole(String token);

    List<AsyncRouterMap> getRouter();
}
