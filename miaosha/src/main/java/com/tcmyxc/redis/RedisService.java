package com.tcmyxc.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 利用 jedis 封装的工具函数
 * @author tcmyxc
 * @date 2021/1/12 23:40
 */
@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;

    /**
     * 获取单个对象
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try {
            // 从连接池获取连接
            jedis = jedisPool.getResource();
            // 生成真正的 key
            String realKey = prefix.getPrefix() + key;
            String value = jedis.get(realKey);
            return strToBean(value, clazz);
        }
        finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置对象
     */
    public <T> boolean set(KeyPrefix prefix, String key, T value){
        // 获取连接池
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realValue = beanToString(value);
            if(realValue == null || realValue.length() <= 0){
                return false;
            }
            // 生成真正的 key
            String realKey = prefix.getPrefix() + key;
            int expireSeconds = prefix.expireSeconds();// 获取过期时间
            if(expireSeconds <= 0){
                jedis.set(realKey, realValue);// 永不过期
            }else {
                jedis.setex(realKey, expireSeconds, realValue);
            }
            return true;
        }
        finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断 key 是否存在
     */
    public <T> boolean exists(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();// 从连接池获取连接
            // 生成真正的 key
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(key);
        }
        finally {
            returnToPool(jedis);
        }
    }

    /**
     * 递增一个元素
     */
    public <T> Long incr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();// 从连接池获取连接
            // 生成真正的 key
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        }
        finally {
            returnToPool(jedis);
        }
    }

    /**
     * 递减一个元素
     */
    public <T> Long decr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();// 从连接池获取连接
            // 生成真正的 key
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        }
        finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除
     * */
    public boolean delete(KeyPrefix prefix, String key) {
        Jedis jedis = null;
        try {
            jedis =  jedisPool.getResource();
            //生成真正的key
            String realKey  = prefix.getPrefix() + key;
            long ret =  jedis.del(realKey);
            return ret > 0;
        }finally {
            returnToPool(jedis);
        }
    }

    private void returnToPool(Jedis jedis) {
        if(jedis != null){
            jedis.close();
        }
    }

    public static  <T> T strToBean(String value, Class<T> clazz) {
        // 参数校验
        if(value == null || value.length() <= 0 || clazz == null){
            return null;
        }
        if(clazz == int.class || clazz == Integer.class){
            return (T) Integer.valueOf(value);
        }else if(clazz == String.class){
            return (T) value;
        }else if(clazz == long.class || clazz == Long.class){
            return (T) Long.valueOf(value);
        }else {
            return JSON.toJavaObject(JSON.parseObject(value), clazz);
        }
    }

    public static  <T> String beanToString(T value) {
        // 如果是基本类型
        if(value == null){
            return null;
        }

        Class<?> clazz = value.getClass();
        if(clazz == int.class || clazz == Integer.class){
            return "" + value;
        }else if(clazz == String.class){
            return (String) value;
        }else if(clazz == long.class || clazz == Long.class){
            return "" + value;
        }
        else {
            return JSON.toJSONString(value);
        }
    }
}
