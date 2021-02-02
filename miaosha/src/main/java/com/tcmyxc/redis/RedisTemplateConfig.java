package com.tcmyxc.redis;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.net.UnknownHostException;

/**
 * @author 徐文祥
 * @date 2021/1/13 19:24
 */
@Configuration
public class RedisTemplateConfig {

    // 编写自己的 RedisTemplate
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        FastJsonRedisSerializer<Object> objectFastJsonRedisSerializer = new FastJsonRedisSerializer<Object>(Object.class);
        // 配置具体的序列化方式
        template.setKeySerializer(objectFastJsonRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
