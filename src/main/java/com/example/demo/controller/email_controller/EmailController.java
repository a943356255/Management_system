package com.example.demo.controller.email_controller;

import com.example.demo.pojo.result.ResultVO;
import com.example.demo.rabbitmq.DelayedProvider;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Resource
    DelayedProvider delayedProvider;

    @RequestMapping("/send")
    public ResultVO<String> testEmail(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        String ip = "";
        if (request.getHeader("x-forwarded-for") == null) {
            ip = request.getRemoteAddr();
        } else {
            ip = request.getHeader("x-forwarded-for");
        }

        String email = (String) map.get("email");
        Long time = (Long) map.get("time");
        String title = (String) map.get("title");

        Long nowTime = new Date().getTime();

        delayedProvider.send((int) (time - nowTime), email + " " + title + "来自" + ip + "发来的消息");
        return new ResultVO<>("success");
    }

}
