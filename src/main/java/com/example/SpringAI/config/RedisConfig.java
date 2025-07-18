package com.example.SpringAI.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String REDIS_HOST;
    @Value("${spring.redis.port}")
    private String REDIS_PORT;
    @Bean
    public JedisPool jedisPool() {
        return new JedisPool(this.REDIS_HOST, Integer.parseInt(this.REDIS_PORT));
    }
}
