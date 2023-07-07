package com.example.demo.controller.login_controller;

import com.example.demo.pojo.UserInfo;
import com.example.demo.pojo.login.AsyncRouterMap;
import com.example.demo.pojo.login.RoleAndMenu;
import com.example.demo.pojo.result.ResultVO;
import com.example.demo.service.login.LoginService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Resource
    private LoginService loginServiceImpl;

    @Resource
    HttpServletRequest request;

    @RequestMapping("/login")
    public ResultVO<UserInfo> userLogin(@RequestBody Map<String, Object> map) {
        return loginServiceImpl.userLogin(map);
    }

    @RequestMapping("/getMenu")
    public ResultVO<List<RoleAndMenu>> getMenu() {
        String token = request.getHeader("token");
        return loginServiceImpl.getMenuByRole(token);
    }

    @RequestMapping("/getRouter")
    public ResultVO<List<AsyncRouterMap>> getRouter() {
        List<AsyncRouterMap> list = loginServiceImpl.getRouter();
        return new ResultVO<>(list);
    }
}
