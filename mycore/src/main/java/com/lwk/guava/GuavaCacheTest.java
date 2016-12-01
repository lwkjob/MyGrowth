package com.lwk.guava;

import com.google.common.cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by lwk on 2016/11/8.
 */
public class GuavaCacheTest {

   private static Logger logger= LoggerFactory.getLogger(GuavaCacheTest.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //缓存接口这里是LoadingCache，LoadingCache在缓存项不存在时可以自动加载缓存
        LoadingCache<Integer, String> strCache
                //CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
                = CacheBuilder.newBuilder()
                //设置并发级别为8，并发级别是指可以同时写缓存的线程数
                .concurrencyLevel(8)
                        //设置写缓存后8秒钟过期
                .expireAfterWrite(8, TimeUnit.SECONDS)
                        //设置缓存容器的初始容量为10
                .initialCapacity(10)
                        //设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
                .maximumSize(100)
                        //设置要统计缓存的命中率
                .recordStats()
                        //设置缓存的移除通知
                .removalListener(new RemovalListener<Object, Object>() {
                    public void onRemoval(RemovalNotification<Object, Object> notification) {
                        System.out.println(notification.getKey() + " was removed, cause is " + notification.getCause());
                    }
                })
                        //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
                .build(
                        new CacheLoader<Integer, String>() {
                            @Override
                            public String load(Integer key) throws Exception {
                                System.out.println("load data: " + key);
                                String str = key + ":cache-value";
                                return str;
                            }
                        }
                );

        strCache.put(2,"fuck1");
        strCache.put(3,"fuck2");

        logger.info(strCache.get(2));
        logger.info(strCache.get(3));

        for (int i = 0; i < 20; i++) {
            //从缓存中得到数据，由于我们没有设置过缓存，所以需要通过CacheLoader加载缓存数据
            String str = strCache.get(1);
            System.out.println(str);
            //休眠1秒
            TimeUnit.SECONDS.sleep(1);
        }

        logger.info(strCache.get(2));
        logger.info(strCache.get(3));
        System.out.println("cache stats:");
        //最后打印缓存的命中率等 情况
        System.out.println(strCache.stats().toString());
    }
}
