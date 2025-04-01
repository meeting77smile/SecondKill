package com.meeting_smile.seckilldemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisConnectionTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void testConnection() {
        try {
            redisTemplate.opsForValue().set("test", "hello");
            System.out.println("Redis 操作成功");
        } catch (Exception e) {
            e.printStackTrace();  // 打印完整错误
        }
    }
}