package com.tcmyxc.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * @author 徐文祥
 * @date 2021/1/27 23:35
 */

@Configuration
public class MQConfig {

    public static final String QUEUE = "queue";
    public static final String MIAOSHA_QUEUE = "miaosha.queue";
    public static final String TOPIC_QUEUE1 = "topic.queue1";
    public static final String TOPIC_QUEUE2 = "topic.queue2";
    public static final String HEADER_QUEUE = "header.queue";

    public static final String TOPIC_EXCHANGE = "topicExchange";
    public static final String FANOUT_EXCHANGE = "fanoutExchange";
    public static final String HEADER_EXCHANGE = "headerExchange";

    // 秒杀队列，direct 模式
    @Bean
    public Queue miaoshaQueue(){
        return new Queue(MIAOSHA_QUEUE, true);
    }

    //交换机模式：Direct 模式
    @Bean
    public Queue queue(){
        return new Queue(QUEUE, true);
    }

    // 交换机模式：Topic 模式
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1, true);
    }

    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2, true);
    }

    // 创建 topic exchange
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    // 1、把消息放到 exchange 里面
    // 2、queue 从 exchange 里面取

    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
    }
    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");
    }

    // fanout 模式：广播模式
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(topicQueue1())
                .to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(topicQueue2())
                .to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding3() {
        return BindingBuilder.bind(queue())
                .to(fanoutExchange());
    }

    // header 模式：
    @Bean
    public HeadersExchange headerExchange(){
        return new HeadersExchange(HEADER_EXCHANGE);
    }

    // header 的队列
    @Bean
    public Queue headerQueue(){
        return new Queue(HEADER_QUEUE, true);
    }

    // 绑定队列
    @Bean
    public Binding headerBinding() {
        Map<String, Object> map = new HashMap<>();
        map.put("header1", "value1");
        map.put("header2", "value2");
        return BindingBuilder.bind(headerQueue())
                .to(headerExchange())
                .whereAll(map)
                .match();
    }

}
