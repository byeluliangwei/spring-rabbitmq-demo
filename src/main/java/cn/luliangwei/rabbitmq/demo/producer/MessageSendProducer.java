package cn.luliangwei.rabbitmq.demo.producer;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.luliangwei.rabbitmq.demo.beans.User;
import cn.luliangwei.rabbitmq.demo.config.RabbitConfig.QueueTemplate;
/*
 * 模拟消息生产者
 */
@Service
public class MessageSendProducer {

//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//    @Autowired
//    private AmqpTemplate amqpTemplate;
    @Autowired
    private QueueTemplate queueTemplate;
    
    public boolean send(User user) {
        queueTemplate.getQueueOneTemplate().convertAndSend("test-direct-exchange1", "test-message-queue1", user);
        queueTemplate.getQueueTwoTemplate().convertAndSend("test-direct-exchange1", "test-message-queue2", user);
//        amqpTemplate.convertAndSend("test-direct-exchange1", "test-message-queue1", user);
//        amqpTemplate.convertAndSend("test-direct-exchange1", "test-message-queue2", user);
//        rabbitTemplate.convertAndSend(user);
        return true;
    }
}
