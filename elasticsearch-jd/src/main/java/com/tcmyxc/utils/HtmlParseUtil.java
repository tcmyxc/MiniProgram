package com.tcmyxc.utils;

import com.tcmyxc.pojo.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.springframework.data.elasticsearch.annotations.DynamicTemplates;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tcmyxc
 * @date 2021/1/6 18:16
 */

@Component
public class HtmlParseUtil {

    public List<Product> parseTmall(String keyword) throws IOException {

        List<Product> productList = new ArrayList<>();
        //要先转码再拼接, 否则URL无法解析 (因为会将url中的符号也一起转码, 无法识别)

        keyword = URLEncoder.encode(keyword, "gb2312");
        System.out.println(keyword);

        //基本的URL
        String url = "https://list.tmall.com/search_product.htm?q=" + keyword;
        //解析URL
        Document document = Jsoup.connect(url).get();
        //System.out.println(document.body().html());
        Elements goodsList = document.select("div.product");
        //System.out.println(goodsList.html());
        for(Element goods : goodsList){
            Product product = new Product();
            String productPrice = goods.select("p.productPrice").text();
            String productImg = goods.getElementsByTag("img").eq(0).attr("data-ks-lazyload");
            //System.out.println(productImg);
            String productTitle = goods.select("p.productTitle").text();
            String productShop = goods.select("div.productShop").text();

            product.setProductPrice(productPrice);
            product.setProductImg(productImg);
            product.setProductTitle(productTitle);
            product.setProductShop(productShop);
            productList.add(product);
        }

        return productList;
    }
}
