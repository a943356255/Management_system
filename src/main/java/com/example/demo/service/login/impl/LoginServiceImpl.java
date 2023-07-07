package com.example.demo.service.login.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.mapper.LoginMapper;
import com.example.demo.pojo.UserInfo;
import com.example.demo.pojo.login.AsyncRouterMap;
import com.example.demo.pojo.login.RoleAndMenu;
import com.example.demo.pojo.result.ResultVO;
import com.example.demo.service.login.LoginService;
import com.example.demo.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoginServiceImpl extends ServiceImpl<LoginMapper, UserInfo> implements LoginService {

    @Resource
    LoginMapper loginMapper;

    @Override
    public ResultVO<UserInfo> userLogin(Map<String, Object> map) {

        String account = (String) map.get("account");
        String password = (String) map.get("password");

        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("account", account);
        UserInfo userInfo = loginMapper.selectOne(wrapper);

        if (userInfo == null || !userInfo.getPassword().equals(password)) {
            return ResultVO.fail("账号或密码错误");
        } else if (userInfo.getStatus() == 0) {
            return ResultVO.fail("账号异常，无法登录，请联系管理员");
        }

        String token = new JwtUtils().getToken(userInfo);
        userInfo.setToken(token);

        return new ResultVO<>(userInfo);
    }

    @Override
    public ResultVO<List<RoleAndMenu>> getMenuByRole(String token) {
        Claims claims = JwtUtils.checkToken(token);
        String role = (String) claims.get("role");
        int roleId = Integer.parseInt(role);
        List<RoleAndMenu> routeList = loginMapper.getRoleAndMenu(roleId);

        System.out.println(routeList.size());

        Map<Integer, Integer> map = new HashMap<>();
        // 遍历第一遍，用map记录father的id对应的下标
        for (int i = 0; i < routeList.size(); i++) {
            if (routeList.get(i).getParentId() == 0) {
                ArrayList<RoleAndMenu> children = new ArrayList<>();
                map.put(routeList.get(i).getId(), i);
                routeList.get(i).setChildren(children);
            }
        }

        // 给children添加元素
        for (RoleAndMenu routeAndRole : routeList) {
            if (routeAndRole.getParentId() != 0) {
                // 获取到父亲元素的下标
                int index = map.get(routeAndRole.getParentId());
                routeList.get(index).getChildren().add(routeAndRole);
            }
        }

        // 去除掉重复的子元素
        ArrayList<RoleAndMenu> list = new ArrayList<>();
        for (RoleAndMenu routeAndRole : routeList) {
            if (routeAndRole.getParentId() == 0) {
                list.add(routeAndRole);
            }
        }

        return new ResultVO<>(list);
    }

    @Override
    public List<AsyncRouterMap> getRouter() {
        return loginMapper.getRouter();
    }
}
