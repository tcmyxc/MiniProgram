package com.tcmyxc.service;

import com.tcmyxc.dao.GoodsDao;
import com.tcmyxc.dao.MiaoshaUserDao;
import com.tcmyxc.domain.Goods;
import com.tcmyxc.domain.MiaoshaGoods;
import com.tcmyxc.vo.GoodsVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 徐文祥
 * @date 2021/1/15 14:08
 */

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId){
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
        miaoshaGoods.setGoodsId(goods.getId());
        int ret = goodsDao.reduceStock(miaoshaGoods);
        return ret > 0;
    }
}
