package com.tcmyxc.controller;

import com.tcmyxc.domain.Goods;
import com.tcmyxc.domain.MiaoshaUser;
import com.tcmyxc.domain.User;
import com.tcmyxc.exception.GlobalException;
import com.tcmyxc.redis.GoodsKey;
import com.tcmyxc.redis.MiaoshaUserKey;
import com.tcmyxc.redis.RedisService;
import com.tcmyxc.result.CodeMsg;
import com.tcmyxc.result.Result;
import com.tcmyxc.service.GoodsService;
import com.tcmyxc.service.MiaoshaUserService;
import com.tcmyxc.vo.GoodsDetailVo;
import com.tcmyxc.vo.GoodsVo;
import org.apache.ibatis.annotations.Param;
import org.omg.CORBA.MARSHAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author 徐文祥
 * @date 2021/1/15 2:01
 */

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    /*
    * 吞吐量：350
    * 5000 * 10
    * 页面缓存
    * */
    @GetMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String goodsList(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user){
        model.addAttribute("user", user);

        // 如果缓存里面有数据，直接返回 html 源代码
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        // 缓存里没有，手动渲染
        // 查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        //return "goods_list";

        IWebContext context =new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
        if(!StringUtils.isEmpty(html)){
            // 存到缓存中去
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    /*
      URL 缓存
     */
    @GetMapping(value = "/to_detail2/{goodsId}", produces = "text/html")
    @ResponseBody
    public String goodsDetail2(HttpServletRequest request,
                              HttpServletResponse response,
                              Model model,
                              @PathVariable("goodsId") long goodsId,
                              MiaoshaUser user){
        // 如果缓存里面有数据，直接返回 html 源代码
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        // 手动渲染
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long currentTime = System.currentTimeMillis();

        int miaoshaStatus = 0;// 秒杀状态
        int remainSeconds = 0;// 距离开始还有多少时间
        // 秒杀还没开始
        if(currentTime < startTime){
            miaoshaStatus = 0;
            remainSeconds = (int) (startTime - currentTime) / 1000;// 转换成秒
        }
        // 秒杀已结束
        else if(currentTime > endTime){
            miaoshaStatus = 2;
            remainSeconds = -1;
        }
        // 秒杀进行时
        else{
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("goods", goods);
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("user", user);

        //return "goods_detail";
        // 缓存里没有，手动渲染
        IWebContext context =new WebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
        if(!StringUtils.isEmpty(html)){
            // 存到缓存中去
            redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
        }
        return html;
    }

    /*
      页面静态化：页面在html中，动态数据通过接口从服务端获取，所以说，服务端只需要写接口就行
     */
    @GetMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> goodsDetail(HttpServletRequest request,
                                             HttpServletResponse response,
                                             Model model,
                                             @PathVariable("goodsId") long goodsId,
                                             MiaoshaUser user){

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        long startTime = goods.getStartDate().getTime();
        long endTime = goods.getEndDate().getTime();
        long currentTime = System.currentTimeMillis();

        int miaoshaStatus = 0;// 秒杀状态
        int remainSeconds = 0;// 距离开始还有多少时间

        // 秒杀还没开始
        if(currentTime < startTime){
            miaoshaStatus = 0;
            remainSeconds = (int) (startTime - currentTime) / 1000;// 转换成秒
        }
        // 秒杀已结束
        else if(currentTime > endTime){
            miaoshaStatus = 2;
            remainSeconds = -1;
        }
        // 秒杀进行时
        else{
            miaoshaStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goods);
        goodsDetailVo.setUser(user);
        goodsDetailVo.setMiaoshaStatus(miaoshaStatus);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        return Result.success(goodsDetailVo);
    }
}
