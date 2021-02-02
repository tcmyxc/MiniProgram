package com.tcmyxc.rabbitmq;

import com.tcmyxc.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 徐文祥
 * @date 2021/1/27 23:34
 */

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;

    public void send(Object message){
        // 将对象转字符串
        String msg = RedisService.beanToString(message);
        // 打印日志
        log.info("send message: " + msg);
        // 指定发送到那个队列
        // void convertAndSend(String routingKey, Object message)
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
    }

    public void sendTopic(Object message){
        // 将对象转字符串
        String msg = RedisService.beanToString(message);
        // 打印日志
        log.info("send topic message: " + msg);
        // 指定发送到那个队列
        // void convertAndSend(String exchange, String routingKey, Object message)
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", msg + "1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", msg + "2");
    }

    public void sendFanout(Object message){
        // 将对象转字符串
        String msg = RedisService.beanToString(message);
        // 打印日志
        log.info("send fanout message: " + msg);
        // 指定发送到那个队列
        // void convertAndSend(String exchange, String routingKey, Object message)
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE , "", msg);
    }

    public void sendHeader(Object message){
        // 将对象转字符串
        String msg = RedisService.beanToString(message);
        // 打印日志
        log.info("send header message: " + msg);
        // 指定发送到那个队列
        // public Message(byte[] body, MessageProperties messageProperties)
        // body 传的是消息，messageProperties 传的是 header 交换机
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("header1", "value1");
        messageProperties.setHeader("header2", "value2");
        Message obj = new Message(msg.getBytes(), messageProperties);
        amqpTemplate.convertAndSend(MQConfig.HEADER_EXCHANGE , "", obj);
    }

    public void sendMiaoshaMessage(MiaoshaMessage miaoshaMessage) {
        // 将对象转字符串
        String msg = RedisService.beanToString(miaoshaMessage);
        // 打印日志
        log.info("send miaosha message: " + msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }
}
