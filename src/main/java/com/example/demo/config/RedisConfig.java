package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 配置 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
//        // 创建 ObjectMapper（推荐的方式）
//        ObjectMapper objectMapper = new ObjectMapper();
//        // 方法1：使用 BasicPolymorphicTypeValidator（推荐）
//        BasicPolymorphicTypeValidator typeValidator =
//                BasicPolymorphicTypeValidator.builder()
//                        .allowIfSubType(Object.class)
//                        .allowIfSubType("com.example.entity") // 允许的包路径
//                        .build();
//        // 使用 String 序列化器作为 key 的序列化器
//        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setConnectionFactory(factory);
        // 设置序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

//        template.afterPropertiesSet();
//        // JSON 序列化配置
//        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        serializer.setObjectMapper(mapper);
//
//        // String 的序列化
//        StringRedisSerializer stringSerializer = new StringRedisSerializer();
//
//        // key 采用 String 序列化
//        template.setKeySerializer(stringSerializer);
//        // hash 的 key 也采用 String 序列化
//        template.setHashKeySerializer(stringSerializer);
//        // value 采用 JSON 序列化
//        template.setValueSerializer(serializer);
//        // hash 的 value 采用 JSON 序列化
//        template.setHashValueSerializer(serializer);
//
//        template.afterPropertiesSet();
        return template;
    }
}