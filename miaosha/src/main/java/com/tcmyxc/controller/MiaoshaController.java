package com.tcmyxc.controller;

import com.sun.deploy.net.HttpResponse;
import com.tcmyxc.access.AccessLimit;
import com.tcmyxc.domain.MiaoshaOrder;
import com.tcmyxc.domain.MiaoshaUser;
import com.tcmyxc.domain.OrderInfo;
import com.tcmyxc.rabbitmq.MQSender;
import com.tcmyxc.rabbitmq.MiaoshaMessage;
import com.tcmyxc.redis.GoodsKey;
import com.tcmyxc.redis.MiaoshaKey;
import com.tcmyxc.redis.RedisService;
import com.tcmyxc.result.CodeMsg;
import com.tcmyxc.result.Result;
import com.tcmyxc.service.GoodsService;
import com.tcmyxc.service.MiaoshaService;
import com.tcmyxc.service.MiaoshaUserService;
import com.tcmyxc.service.OrderService;
import com.tcmyxc.util.MD5Util;
import com.tcmyxc.util.UUIDUtil;
import com.tcmyxc.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 徐文祥
 * @date 2021/1/15 21:38
 */

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();


    // 获取图片验证码
    @GetMapping("/verifyCode")
    @ResponseBody
    public Result<String> getVerifyCode(HttpServletResponse response,
                                        MiaoshaUser user,
                                        @RequestParam("goodsId") long goodsId){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
        try {
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "JPEG", outputStream);
            outputStream.flush();
            outputStream.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

    // 获取秒杀地址接口
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @GetMapping("/path")
    @ResponseBody
    public Result<String> getPath(HttpServletRequest request, MiaoshaUser user,
                                  @RequestParam("goodsId") long goodsId,
                                  @RequestParam("verifyCode") int verifyCode){
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
//        // 接口防刷限流
//        // 我们定义 5 秒访问最多 5 次
//        // 查询访问的次数
//        String requestURI = request.getRequestURI();
//        String key = requestURI + "_" + user.getId();
//        Integer cnt = redisService.get(MiaoshaKey.access, key, Integer.class);
//        if(cnt == null){
//            redisService.set(MiaoshaKey.access, key, 1);
//        }
//        else if(cnt < 5){
//            redisService.incr(MiaoshaKey.access, key);
//        }
//        else{
//            return Result.error(CodeMsg.ACCESS_LIMITED);
//        }

        // 校验验证码
        boolean checkVerifyCode = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if(checkVerifyCode == false){
            return Result.error(CodeMsg.VERIFY_CODE_ERROR);
        }


        // 随机生成一个 UUID 即可
        String path = miaoshaService.createPath(user.getId(), goodsId);
        return Result.success(path);
    }

    /**
     *
     * @param model
     * @param user
     * @param goodsId
     * @return 秒杀成功，返回订单号 orderId, 失败的话，返回 -1, 排队中，返回 0
     */
    @GetMapping("/result")
    @ResponseBody
    public Result<Long> getResult(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){
        model.addAttribute("user", user);
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 轮询
        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }
    /*
    * 吞吐量：2500 左右
    * 改造之后：3200 左右
    * */
    @PostMapping("/{path}/do_miaosha")
    @ResponseBody
    public Result<Integer> doMiaosha(Model model, MiaoshaUser user,
                                     @RequestParam("goodsId") long goodsId,
                                     @PathVariable("path") String path){
        model.addAttribute("user", user);
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        // 验证 path 参数
        boolean checkPath = miaoshaService.checkPath(user, goodsId, path);
        if(checkPath == false){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        // 先判断秒杀结束的标记是否为 true，可以减少 redis 访问
        boolean isOver = localOverMap.get(goodsId);
        if(isOver){
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        // 收到请求把缓存里面的库存减掉（预减库存）
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        // 秒杀失败
        if(stock < 0){
            localOverMap.put(goodsId, true);// 库存不足，秒杀结束标记为 true
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        // 判断是否已经秒杀过了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA_ORDER);
        }
        // 入队
        MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
        miaoshaMessage.setUser(user);
        miaoshaMessage.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(miaoshaMessage);

        return Result.success(0);// 0 代表排队中
/*        // 判断商品是否还有库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        // 秒杀失败
        if(stock <= 0){
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

        // 判断是否已经秒杀过了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if(order != null){
            return Result.error(CodeMsg.REPEATE_MIAOSHA_ORDER);
        }

        // 减库存、下订单、生成订单信息（这三步应该当成一个事务处理）
        OrderInfo orderInfo = miaoshaService.doMiaosha(user, goods);
        return Result.success(orderInfo);

 */


    }

    // 系统初始话的时候做一些事情
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null){
            return;
        }
        // 将商品数量加载到缓存
        for(GoodsVo goods : goodsList){
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }
}
