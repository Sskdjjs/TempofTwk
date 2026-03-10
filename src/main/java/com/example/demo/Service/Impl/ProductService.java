package com.example.demo.Service.Impl;

// src/main/java/com/example/redisdemo/service/ProductService.java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ProductService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private Map<Long, String> productDB = new HashMap<>();

    public ProductService() {
        // 模拟数据库
        productDB.put(1L, "iPhone 15 Pro");
        productDB.put(2L, "MacBook Pro");
        productDB.put(3L, "iPad Air");
        productDB.put(4L, "AirPods Pro");
    }

    /**
     * 获取商品详情 - 带缓存
     * 模拟：第一次从数据库读（慢），后续从缓存读（快）
     */
    public String getProductDetail(Long productId) {
        String cacheKey = "product:detail:" + productId;

        // 1. 先查缓存
        String cached = (String) redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            System.out.println("🎯 [缓存命中] 商品 " + productId + " 从 Redis 获取");
            return cached + " (来自缓存)";
        }

        // 2. 缓存不存在，模拟从数据库读取（慢操作）
        System.out.println("⏳ [缓存未命中] 商品 " + productId + " 从数据库读取");
        try {
            Thread.sleep(1000); // 模拟数据库查询耗时 1秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String product = productDB.get(productId);
        if (product == null) {
            return "商品不存在";
        }

        // 3. 存入缓存，下次就快了
        redisTemplate.opsForValue().set(cacheKey, product, 10, TimeUnit.MINUTES);
        System.out.println("💾 商品 " + productId + " 已存入 Redis 缓存");

        return product + " (来自数据库)";
    }

    /**
     * 商品搜索 - 带结果缓存
     */
    public String searchProducts(String keyword) {
        String cacheKey = "search:" + keyword;

        // 检查缓存
        String cached = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached + " (缓存结果)";
        }

        // 模拟搜索（耗时）
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 模拟搜索结果
        String result = "搜索 '" + keyword + "' 的结果: 找到3个商品";

        // 缓存搜索结果，2分钟过期
        redisTemplate.opsForValue().set(cacheKey, result, 2, TimeUnit.MINUTES);

        return result;
    }

    /**
     * 商品浏览量统计
     */
    public void recordProductView(Long productId) {
        String key = "product:views:" + productId;
        redisTemplate.opsForValue().increment(key);
    }

    public Long getProductViews(Long productId) {
        String key = "product:views:" + productId;
        Object views = redisTemplate.opsForValue().get(key);
        return views != null ? Long.parseLong(views.toString()) : 0L;
    }
}