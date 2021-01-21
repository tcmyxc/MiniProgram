package com.tcmyxc.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ES配置类
 * 1、找对象
 * 2、放到Spring中待用
 * 3、如果是SpringBoot，就先分析源码
 *      xxxAutoConfiguration  xxxProperties
 * @author tcmyxc
 * @date 2021/1/5 6:44
 */
@Configuration
public class ElasticsearchConfig {

    // 把对象注入Spring
    // spring <beans id是方法名， class 是返回值
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        return client;
    }
}
