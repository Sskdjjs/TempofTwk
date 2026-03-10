package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisLock {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public boolean tryLock(String key, long expireSeconds) {
        String lockKey = "lock:" + key;
        try {
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", expireSeconds, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(success);
        } catch (Exception e) {
            log.error("获取锁失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 释放锁
     */
    public void unlock(String key) {
        String lockKey = "lock:" + key;
        try {
            redisTemplate.delete(lockKey);
        } catch (Exception e) {
            log.error("释放锁失败: key={}", key, e);
        }
    }

    /**
     * 获取锁（带重试）
     */
    public boolean tryLockWithRetry(String key, long expireSeconds,
                                    int maxRetry, long retryIntervalMs) {
        int retryCount = 0;
        while (retryCount < maxRetry) {
            if (tryLock(key, expireSeconds)) {
                return true;
            }
            retryCount++;
            try {
                Thread.sleep(retryIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}
