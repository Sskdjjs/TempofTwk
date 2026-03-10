import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.Demo1Application;
import com.example.demo.entity.Answer;
import com.example.demo.mapper.AnswerMapper;
import org.springframework.context.ApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

@SpringBootTest(classes = Demo1Application.class)
public class applicationtest {

    private StringRedisTemplate stringRedisTemplate;
    private  AnswerMapper answerMapper;
    @Autowired
    private ApplicationContext applicationContext;
    @BeforeEach
    void setUp() {
        answerMapper = applicationContext.getBean(AnswerMapper.class);
        // 1. 创建连接工厂
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory();
        connectionFactory.setHostName("localhost");
        connectionFactory.setPort(6379);
        connectionFactory.setDatabase(0);
        connectionFactory.afterPropertiesSet();  // 重要！

        // 2. 创建 StringRedisTemplate
        stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(connectionFactory);
        stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
        stringRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        stringRedisTemplate.setHashValueSerializer(new StringRedisSerializer());
        stringRedisTemplate.afterPropertiesSet();  // 重要！

        System.out.println("✅ RedisTemplate创建成功");
    }
    @Test
    void testWithStringRedisTemplate() {
        Long answerId = 1L;
        String key = "answer:vote:" + answerId;

        // 设置数据
//        stringRedisTemplate.opsForHash().put(key, "up", "30");
//        stringRedisTemplate.opsForHash().put(key, "down", "5");

        // 获取数据
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
        System.out.println("数据: " + entries);

        // 注意：StringRedisTemplate返回的都是String
        String upValue = (String) entries.getOrDefault("up", "0");
        String downValue = (String) entries.getOrDefault("down", "0");

        int upVotes = Integer.parseInt(upValue);
        int downVotes = Integer.parseInt(downValue);

        System.out.println("结果: up=" + upVotes + ", down=" + downVotes);
    }
    @Test
    public void updateAnswerStats() {
            Long answerId = 1L;
            // 从Redis获取最新的投票统计
            Map<String, Integer> stats = getVoteStatsFromRedis(answerId);
//            UpdateWrapper<Answer> updateWrapper = new UpdateWrapper<>();
//            updateWrapper.eq("id",answerId);
//            updateWrapper.set("upvote_count",stats.get("upvoteCount"));
//            updateWrapper.set("downvote_count",stats.get("downCount"));

            LambdaUpdateWrapper<Answer> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(Answer::getId,answerId)
                    .set(Answer::getUpvoteCount,stats.get("upvoteCount"))
                    .set(Answer::getDownvoteCount,stats.get("downvoteCount"));
            answerMapper.update(lambdaUpdateWrapper);
            System.out.println("up = "+stats.get("upvoteCount"));
//            log.debug("更新答案统计: answerId={}, up={}, down={}",
//                    answerId, stats.get("upvoteCount"), stats.get("downvoteCount"));


    }
    private Map<String, Integer> getVoteStatsFromRedis(Long answerId) {
        String key = "answer:vote:"+ answerId;
        Map<Object,Object> redisVotes = stringRedisTemplate.opsForHash().entries(key);

        int upVotes = Integer.parseInt(redisVotes.getOrDefault("up", "0").toString());
        int downVotes = Integer.parseInt(redisVotes.getOrDefault("down", "0").toString());

        return Map.of("upvoteCount", upVotes, "downvoteCount", downVotes);
    }
}
