package com.example.demo.controller.menu_controller;

import com.example.demo.pojo.result.ResultVO;
import com.example.demo.service.menu.MenuService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Resource
    MenuService menuServiceImpl;

    @RequestMapping("/addMenu")
    public ResultVO<Integer> addMenu(@RequestBody Map<String, Object> map) {
        return new ResultVO<>(menuServiceImpl.addMenu(map));
    }

}
