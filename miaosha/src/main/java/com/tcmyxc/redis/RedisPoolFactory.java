package com.tcmyxc.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author 徐文祥
 * @date 2021/1/13 2:33
 */

@Service
public class RedisPoolFactory {

    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool jedisPoolFactory(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000);
        JedisPool jedisPool = new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(),
                redisConfig.getTimeout() * 1000, redisConfig.getPassword(), 0);

        return jedisPool;
    }
}
