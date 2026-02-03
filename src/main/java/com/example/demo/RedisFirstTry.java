package com.example.demo;

// 创建：src/main/java/com/example/redisdemo/RedisFirstTry.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class RedisFirstTry {
    public static void main(String[] args) {
        SpringApplication.run(RedisFirstTry.class, args);
    }
}

@Component
class RedisTester implements CommandLineRunner {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🎉 Redis 连接测试开始！");

        // 测试1：写入数据
        redisTemplate.opsForValue().set("greeting", "Hello, Redis!");
        System.out.println("✅ 数据已写入 Redis");

        // 测试2：读取数据
        String value = redisTemplate.opsForValue().get("greeting");
        System.out.println("📖 从Redis读取: " + value);

        // 测试3：检查Redis中是否真的有数据
        System.out.println("🔍 检查Redis是否存储成功...");

        // 测试4：自增计数器
        redisTemplate.opsForValue().set("visitorCount", "0");
        redisTemplate.opsForValue().increment("visitorCount");
        String count = redisTemplate.opsForValue().get("visitorCount");
        System.out.println("👥 访问次数: " + count);

        System.out.println("🎊 Redis 集成成功！");
        System.out.println("你已经在 Spring Boot 中使用 Redis 了！");
    }
}