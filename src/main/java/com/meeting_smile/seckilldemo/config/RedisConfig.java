package com.meeting_smile.seckilldemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<> ();
        //设置redis key的序列化(序列化：把对象转换为字节序列的过程称为对象的序列化)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //value序列化
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //hash类型的key序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //hash类型value序列化
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        //注入连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }
}
