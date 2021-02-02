package com.tcmyxc.rabbitmq;

import com.tcmyxc.domain.MiaoshaOrder;
import com.tcmyxc.domain.MiaoshaUser;
import com.tcmyxc.domain.OrderInfo;
import com.tcmyxc.redis.RedisService;
import com.tcmyxc.result.CodeMsg;
import com.tcmyxc.result.Result;
import com.tcmyxc.service.GoodsService;
import com.tcmyxc.service.MiaoshaService;
import com.tcmyxc.service.MiaoshaUserService;
import com.tcmyxc.service.OrderService;
import com.tcmyxc.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 徐文祥
 * @date 2021/1/27 23:35
 */

@Service
public class MQReceiver {

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    // 声明监听的队列
    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void miaoshaReceive(String message){
        log.info("receive message:" + message);
        MiaoshaMessage miaoshaMessage = RedisService.strToBean(message, MiaoshaMessage.class);
        MiaoshaUser user = miaoshaMessage.getUser();
        long goodsId = miaoshaMessage.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        // 没有库存了
        if(stock <= 0){
            return;
        }
        // 判断是否已经秒杀过了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if(order != null){
            return;
        }

        // 减库存、下订单、生成订单信息（这三步应该当成一个事务处理）
        OrderInfo orderInfo = miaoshaService.doMiaosha(user, goods);
    }

    // 声明监听的队列
    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message){
        log.info("receive message:" + message);
    }

    // 声明监听的队列
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void topicReceive1(String message){
        log.info("topic queue1 message:" + message);
    }

    // 声明监听的队列
    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void topicReceive2(String message){
        log.info("topic queue2 message:" + message);
    }

    // 声明监听的队列
    @RabbitListener(queues = MQConfig.HEADER_QUEUE)
    public void headerReceive(byte[] message){
        log.info("header queue message:" + new String(message));
    }
}
