package com.channelsoft.ccod;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring-redis.xml");
        RedisCacheManager redisManager = (RedisCacheManager) ctx.getBean("redisCacheManager");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("k1","v1");
        map.put("k2","v2");
        map.put("k3","v3");
        redisManager.hmset("map1",map);
        Map<Object,Object> result = redisManager.hmget("map1");
        System.out.println(result);
    }
}
