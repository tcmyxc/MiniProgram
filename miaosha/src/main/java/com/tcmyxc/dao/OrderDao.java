package com.tcmyxc.dao;

import com.tcmyxc.domain.MiaoshaOrder;
import com.tcmyxc.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

/**
 * @author 徐文祥
 * @date 2021/1/15 22:17
 */

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
    MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement="select last_insert_id()")
    long insertOrder(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    long insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);
}
