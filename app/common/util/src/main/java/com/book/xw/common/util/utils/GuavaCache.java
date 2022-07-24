package com.book.xw.common.util.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class GuavaCache {

    private Cache<String, Object> commonCache = null;

    //代理此bean时会首先执行该初始化方法
    @PostConstruct
    public void init() {
        commonCache = CacheBuilder.newBuilder()
                //设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
                .concurrencyLevel(1)
                //设置缓存容器的初始化容量为10（可以存10个键值对）
                .initialCapacity(5)
                //最大缓存容量是500,超过500后会安装LRU策略-最近最少使用，
                .maximumSize(500)
                //设置写入缓存后2分钟后过期
//                .expireAfterWrite(120, TimeUnit.SECONDS)
                .build();
    }

    public void setCache(String key, Object value) {
        commonCache.put(key, value);
    }

    public Object getCache(String key) {
        return commonCache.getIfPresent(key);
    }

    public void invalidate(Object key) {
        commonCache.invalidate(key);
    }
}
