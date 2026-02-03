package com.example.demo.Controller;

// src/main/java/com/example/redisdemo/controller/RedisDemoController.java
import com.example.demo.entity.TempUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redis")
public class RedisDemoController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 1. 测试接口 - 感受 Redis 速度
     */
//    @GetMapping("/test")
//    public String test() {
//        long start = System.currentTimeMillis();
//
//        // 写入 Redis
//        redisTemplate.opsForValue().set("api_test", "Redis is working!");
//
//        // 读取 Redis
//        String value = (String) redisTemplate.opsForValue().get("api_test");
//
//        long end = System.currentTimeMillis();
//
//        return String.format(" Redis 操作成功！<br>" +
//                        "值: %s <br>" +
//                        "耗时: %d 毫秒 <br>" +
//                        "感受到 Redis 的速度了吗？",
//                value, end - start);
//    }
    @GetMapping(value = "/test", produces = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8")
    public String test() {
        long start = System.currentTimeMillis();

        redisTemplate.opsForValue().set("api_test", "Redis is working!");
        String value = (String) redisTemplate.opsForValue().get("api_test");

        long end = System.currentTimeMillis();

        return String.format("✅ Redis 操作成功！<br>" +
                        "值: %s <br>" +
                        "耗时: %d 毫秒 <br>" +
                        "🎉 感受到 Redis 的速度了吗？",
                value, end - start);
    }

    /**
     * 2. 缓存用户 - 体验对象存储
     */
    @PostMapping("/user/{id}")
    public String cacheUser(@PathVariable Long id) {
        TempUser user = TempUser.createSample(id);
        String key = "user:" + id;

        // 存储到 Redis，设置5分钟过期
        redisTemplate.opsForValue().set(key, user, 5, TimeUnit.MINUTES);

        return String.format("✅ 用户已缓存到 Redis！<br>" +
                        "用户ID: %d <br>" +
                        "用户名: %s <br>" +
                        "缓存键: %s <br>" +
                        "过期时间: 5分钟",
                user.getId(), user.getName(), key);
    }

    /**
     * 3. 获取缓存用户 - 体验快速读取
     */
    @GetMapping("/user/{id}")
    public Object getUser(@PathVariable Long id) {
        String key = "user:" + id;
        TempUser user = (TempUser) redisTemplate.opsForValue().get(key);

        if (user == null) {
            return "❌ 用户不存在或已过期，请先调用 POST /api/redis/user/" + id;
        }

        return user;
    }

    /**
     * 4. 计数器 - 体验原子操作
     */
    @PostMapping("/counter/{name}/increment")
    public String incrementCounter(@PathVariable String name) {
        String key = "counter:" + name;
        Long count = redisTemplate.opsForValue().increment(key);

        return String.format("🔢 计数器 '%s' 已增加<br>" +
                        "当前值: %d<br>" +
                        "这个操作是原子性的，不会出错！",
                name, count);
    }

    @GetMapping("/counter/{name}")
    public String getCounter(@PathVariable String name) {
        String key = "counter:" + name;
        Object count = redisTemplate.opsForValue().get(key);

        if (count == null) {
            count = "0 (未初始化)";
        }

        return String.format("计数器 '%s': %s", name, count);
    }

    /**
     * 5. 排行榜 - 体验 Sorted Set
     */
    @PostMapping("/leaderboard/{player}/{score}")
    public String addToLeaderboard(@PathVariable String player,
                                   @PathVariable Double score) {
        String key = "game:leaderboard";
        redisTemplate.opsForZSet().add(key, player, score);

        // 获取排名（从0开始）
        Long rank = redisTemplate.opsForZSet().reverseRank(key, player);

        return String.format("🏆 玩家 '%s' 得分 %.1f<br>" +
                        "当前排名: 第%d名<br>" +
                        "🎮 Redis 的排序功能很强大！",
                player, score, rank + 1);
    }

    @GetMapping("/leaderboard/top/{n}")
    public Object getTopPlayers(@PathVariable Integer n) {
        String key = "game:leaderboard";
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, n - 1);
    }

    /**
     * 6. 查看 Redis 中的键
     */
    @GetMapping("/keys")
    public Object listKeys() {
        return redisTemplate.keys("*");
    }

    /**
     * 7. 清空测试数据
     */
    @DeleteMapping("/clear")
    public String clearAll() {
        redisTemplate.delete(redisTemplate.keys("*"));
        return "🧹 所有测试数据已清空";
    }
}
