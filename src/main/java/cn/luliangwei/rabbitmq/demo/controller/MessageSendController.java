package cn.luliangwei.rabbitmq.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cn.luliangwei.rabbitmq.demo.beans.User;
import cn.luliangwei.rabbitmq.demo.producer.MessageSendProducer;

@RestController
public class MessageSendController {

    @Autowired
    MessageSendProducer producer;
    
    /*
     * 模拟消息从客户端传过来，经生产者发送，然后消费者消费
     * 
     * 这里其实可以直接将生产者放在这里发送，不过为了形象，就封装了一下
     */
    @PostMapping("/test/send-message")
    public String sendMessageToQueue(@RequestBody User user) {
        if(producer.send(user)) return "send success";
        else return "send failure";
    }
}
