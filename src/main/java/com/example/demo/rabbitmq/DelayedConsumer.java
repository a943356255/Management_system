package com.example.demo.rabbitmq;

import com.example.demo.service.email.EmailService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class DelayedConsumer {

    @Resource
    EmailService emailServiceImpl;

    @Value("${spring.mail.username}")
    private String emailAccount;

    // 这里是监听队列，即交换机绑定的队列
    @RabbitListener(queues = "test-delayed-queue")
    public void testListenerDelayedMessage(Message message) {
        byte[] body = message.getBody();
        System.out.println(LocalDateTime.now() + new String(body));
        String str = new String(body);
        String[] arr = str.split(" ");
        try {
            emailServiceImpl.sendSimpleMail(emailAccount, arr[0], "待办事件提醒", arr[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}