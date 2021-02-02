package com.tcmyxc.redis;

/**
 * @author 徐文祥
 * @date 2021/1/13 19:50
 */
public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;
    private String prefix;

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    // 默认永不过期
    public BasePrefix(String prefix) {
        this(0, prefix);
    }

    @Override
    public int expireSeconds() {
        // 默认 0 代表永不过期
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        // 使用类名实现唯一前缀
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
