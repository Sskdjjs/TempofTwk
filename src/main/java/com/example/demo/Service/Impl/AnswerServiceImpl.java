package com.example.demo.Service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.demo.DTO.response.AnswerVO;
import com.example.demo.Service.AnswerService;
import com.example.demo.entity.Answer;
import com.example.demo.entity.AnswerVote;
import com.example.demo.entity.Question;
import com.example.demo.mapper.AnswerMapper;
import com.example.demo.mapper.AnswerVoteMapper;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.utils.RedisLock;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerMapper answerMapper;
    private final QuestionMapper questionMapper;
    private final AnswerVoteMapper answerVoteMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisLock redisLock;
    // Redis键前缀
    private static final String ANSWER_VOTE_KEY = "answer:vote:";
    private static final String USER_VOTE_KEY = "user:vote:";
    @Override
    public AnswerVO createAnswer(Long questionId, Long userId, String content) {
        Answer answer = Answer.builder()
                .questionId(questionId)
                .userId(userId)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        answerMapper.insert(answer);
        questionMapper.update(null,
                new UpdateWrapper<Question>()
                .eq("id",questionId)
                .setSql("answer_count = answer_count + 1"));
        return convertToVO(answer,userId);
    }

    /**
     * 未实现分页查询
     * @param questionId
     * @param currentUserId
     * @return
     */
    @Override
    public List<AnswerVO> getAnswersByQuestionId(Long questionId, Long currentUserId) {
        List<Answer> list = answerMapper.selectList(new LambdaQueryWrapper<Answer>()
                .eq(Answer::getQuestionId,questionId)
                .eq(Answer::getIsDeleted,false)
                .orderByDesc(Answer::getCreatedAt));
        List<AnswerVO> answerVOList = new ArrayList<>();
        for (Answer answer : list) {
            AnswerVO answerVO = new AnswerVO();
            BeanUtil.copyProperties(answer,answerVO);
            answerVOList.add(answerVO);
        }
        return answerVOList;
    }

    @Override
    public void deleteAnswer(Long answerId, Long userId) {
        var a = answerMapper.update(new LambdaUpdateWrapper<Answer>()
                .set(Answer::getIsDeleted,true)
                .eq(Answer::getId,answerId)
                .eq(Answer::getUserId,userId));
        if (a > 0) {
            log.info("删除成功");
        }
    }

    @Override
    public void voteAnswer(Long answerId, Long userId, Integer voteType) {
        // 🔒 1. 加锁：防止同一用户重复投票
        String lockKey = "vote:" + answerId + ":" + userId;
        boolean locked = false;
        try {
            // 尝试获取锁，5秒超时
            locked = redisLock.tryLock(lockKey, 5);
            if (!locked) {
                throw new RuntimeException("操作过于频繁，请稍后重试");
            }
            // 2. 在Redis中处理投票
            handleVoteInRedis(answerId, userId, voteType);
            // 3. 异步同步到数据库（带重试）
            asyncVoteWithRetry(answerId, userId, voteType);
        } finally {
            // 5. 释放锁
            if (locked) {
                redisLock.unlock(lockKey);
            }
        }
    }

    private void asyncVoteWithRetry(Long answerId, Long userId, Integer voteType) {
        CompletableFuture.runAsync(() -> {
            int maxRetry = 5;
            int retryCount = 0;
            int baseDelay = 1000; // 基础延迟1秒

            while (retryCount < maxRetry) {
                try {
                    // 尝试同步到数据库（事务性操作）
                    syncVoteToDatabase(answerId, userId, voteType);

                    log.info("数据库同步成功: answerId={}, userId={}, retryCount={}",
                            answerId, userId, retryCount);
                    return; // 成功就退出

                } catch (Exception e) {
                    retryCount++;
                    log.warn("数据库同步失败，第{}次重试: answerId={}, userId={}",
                            retryCount, answerId, userId, e);

                    if (retryCount >= maxRetry) {
                        // 最终失败：记录到失败表
                        log.info("重试失败");
                    } else {
                        // 退避策略：递增等待时间
                        try {
                            int delay = baseDelay * retryCount; // 1s, 2s, 3s, 4s, 5s
                            Thread.sleep(delay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            log.error("重试被中断", ie);
                            return;
                        }
                    }
                }
            }
        }).exceptionally(e -> {
            log.error("异步任务异常", e);
            return null;
        });
    }
    @Transactional(rollbackFor = Exception.class)
    public void syncVoteToDatabase(Long answerId, Long userId, Integer voteType) {
        // 1. 查询是否已有投票记录
        AnswerVote existVote = answerVoteMapper.selectOne(
                new LambdaQueryWrapper<AnswerVote>()
                        .eq(AnswerVote::getAnswerId, answerId)
                        .eq(AnswerVote::getUserId, userId)
        );
        // 2. 保存/更新投票记录
        if (existVote == null) {
            // 新增投票
            AnswerVote answerVote = AnswerVote.builder()
                    .answerId(answerId)
                    .userId(userId)
                    .voteType(voteType)
                    .createdAt(LocalDateTime.now())
                    .build();
            int rows = answerVoteMapper.insert(answerVote);
            if (rows > 0) {
                log.info("数据库新增投票记录: answerId={}, userId={}", answerId, userId);
            }
        } else {
            // 更新投票
            if (!existVote.getVoteType().equals(voteType)) {
                existVote.setVoteType(voteType);
                int rows = answerVoteMapper.updateById(existVote);
                if (rows > 0) {
                    log.info("数据库更新投票记录: answerId={}, userId={}, old={}, new={}",
                            answerId, userId, existVote.getVoteType(), voteType);
                }
            }
        }
        // 3. 从数据库重新统计（保证准确）
        Map<String, Long> stats = answerVoteMapper.countVotesByAnswerId(answerId);
        long upVotes = stats != null ? stats.getOrDefault("up_count", 0l) : 0;
        long downVotes = stats != null ? stats.getOrDefault("down_count", 0l) : 0;
        // 4. 更新答案表的统计字段
        LambdaUpdateWrapper<Answer> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Answer::getId, answerId)
                .set(Answer::getUpvoteCount, upVotes)
                .set(Answer::getDownvoteCount, downVotes)
                .set(Answer::getUpdatedAt, LocalDateTime.now());

        int rows = answerMapper.update(null, updateWrapper);
        if (rows > 0) {
            log.info("更新答案统计: answerId={}, up={}, down={}", answerId, upVotes, downVotes);
        }
        // 5. 同步更新Redis中的统计（保持缓存一致）
        updateAnswerStatsInRedis(answerId, upVotes, downVotes);

    }
    /**
     * 更新Redis中的答案统计
     */
    private void updateAnswerStatsInRedis(Long answerId, long upVotes, long downVotes) {
        String key = ANSWER_VOTE_KEY + answerId;

        Map<String, String> voteMap = new HashMap<>();
        voteMap.put("up", String.valueOf(upVotes));
        voteMap.put("down", String.valueOf(downVotes));

        redisTemplate.opsForHash().putAll(key, voteMap);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }
    /**
     * 在Redis中处理投票
     */
    private void handleVoteInRedis(Long answerId, Long userId, Integer voteType) {
        String answerKey = ANSWER_VOTE_KEY + answerId;
        String userKey = USER_VOTE_KEY + answerId + ":" + userId;

        // 1. 先确保Redis缓存已初始化（从数据库加载）
        //redis里数据为真,有是有,没有就是没有
//        ensureCacheInitialized(answerId, userId);
//        // 2. 执行投票逻辑
//        executeVoteInRedis(answerId, userId, voteType);
//        //直接执行redis操作,通过带重试机制的异步同步数据库确保数据最终一致性
        // 1. 获取当前投票
        String currentVoteStr = redisTemplate.opsForValue().get(userKey);
        Integer currentVote = currentVoteStr != null ?
                Integer.parseInt(currentVoteStr) : null;
        if (currentVote != null && currentVote.equals(voteType)) {
            log.info("重复相同投票");
            return;

        }
        // 3. 取消旧投票
        if (currentVote != null) {
            String oldField = currentVote.equals(1) ? "up" : "down";
            redisTemplate.opsForHash().increment(answerKey, oldField, -1);
        }
        // 4. 添加新投票
        String newField = voteType.equals(1) ? "up" : "down";
        redisTemplate.opsForHash().increment(answerKey, newField, 1);

        // 5. 更新用户投票
        redisTemplate.opsForValue().set(userKey, voteType.toString(), 7, TimeUnit.DAYS);
        // 6. 确保答案统计key有过期时间
        redisTemplate.expire(answerKey, 1, TimeUnit.HOURS);

        log.info("Redis投票成功: answerId={}, userId={}, from={} to={}",
                answerId, userId, currentVote, voteType);
    }




    @Override
    public void updateVoteCount(Long answerId) {

    }

    @Override
    public AnswerVO convertToVO(Answer answer, Long currentUserId) {
        AnswerVO answerVO = new AnswerVO();
        BeanUtil.copyProperties(answer,answerVO);
        return answerVO;
    }
}
