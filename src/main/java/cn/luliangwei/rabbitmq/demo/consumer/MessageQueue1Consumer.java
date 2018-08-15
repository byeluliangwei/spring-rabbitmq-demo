package cn.luliangwei.rabbitmq.demo.consumer;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;

import cn.luliangwei.rabbitmq.demo.beans.User;

/*
 * 消费者对收到的消息进行异步处理
 */
@Service
@RabbitListener(containerFactory = "rabbitListenerContainerFactory", queues = { "test-message-queue1" })
public class MessageQueue1Consumer {

    /*
     * 消费队列中的消息
     */
    @RabbitHandler
    public void consume(@Payload User user, Message message, Channel channel) {
        try {
            System.out.println("===============================================");
            System.out.println("消费者收到生产者投递到队列1中的消息：" + user.toString());
            System.out.println("消费消息：");
            System.out.println("姓名：" + user.getName());
            System.out.println("年龄：" + user.getAge());
            System.out.println("地址：" + user.getAddress());
            System.out.println("联系方式：" + user.getPhone());
            System.out.println("===============================================");

            // 消费完成后手动确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            // 消费失败，需要做异常处理
        }
    }
}
