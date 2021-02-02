package com.tcmyxc.service;

import com.tcmyxc.domain.MiaoshaOrder;
import com.tcmyxc.domain.MiaoshaUser;
import com.tcmyxc.domain.OrderInfo;
import com.tcmyxc.redis.MiaoshaKey;
import com.tcmyxc.redis.RedisService;
import com.tcmyxc.util.MD5Util;
import com.tcmyxc.util.UUIDUtil;
import com.tcmyxc.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author 徐文祥
 * @date 2021/1/15 22:07
 */

@Service
public class MiaoshaService {

    // 自己的 service 调用自己的 dao，如果实在要用其他的，使用其对应的 service
    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo doMiaosha(MiaoshaUser user, GoodsVo goods) {
        // 减库存，下订单，写入秒杀订单
        boolean success = goodsService.reduceStock(goods);
        if(success){
            return orderService.createOrder(user, goods);
        }else {
            setGoodsOver(goods.getId());// 设置卖完的标记
            return null;
        }
    }

    public long getMiaoshaResult(Long userId, long goodsId) {
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
        if(order != null){
            // 秒杀成功
            return order.getOrderId();
        }
        else{
            // 商品是否卖完了
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1;// 卖完了
            }
            else{
                return 0;// 继续轮询
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(MiaoshaKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.isGoodsOver, "" + goodsId);
    }

    public boolean checkPath(MiaoshaUser user, long goodsId, String path) {
        if(user == null || path == null){
            return false;
        }
        String oldPath = redisService.get(MiaoshaKey.getMiaoshaPath, "" + user.getId() + "_" + goodsId, String.class);
        return path.equals(oldPath);
    }

    public String createPath(Long userId, long goodsId) {
        String path = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(MiaoshaKey.getMiaoshaPath, "" + userId + "_" + goodsId, path);
        return path;
    }

    // 验证码
    public BufferedImage createVerifyCode(MiaoshaUser user, long goodsId) {
        if(user == null || goodsId <= 0){
            return null;
        }

        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion 生成 50 个干扰点
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    // 计算表达式结果
    private int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (int) engine.eval(exp);
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[]{'+','-', '*'};

    // 生成验证码
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);

        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];

        String express = "" + num1 + op1 +num2 + op2 +num3;
        return express;
    }

    // 校验验证码
    public boolean checkVerifyCode(MiaoshaUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <= 0){
            return false;
        }
        Integer oldCode = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if(oldCode == null || oldCode - verifyCode != 0){
            return false;
        }
        // 删掉这个一次性的验证码
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId() + "," + goodsId);
        return true;
    }
}
