package com.example.demo.Service;

import com.example.demo.DTO.response.AnswerVO;
import com.example.demo.entity.Answer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

// AnswerService.java - 复制粘贴
@Service
public interface AnswerService {


    // 创建回答
    AnswerVO createAnswer(Long questionId, Long userId, String content) ;

    // 获取问题回答列表
     List<AnswerVO> getAnswersByQuestionId(Long questionId, Long currentUserId);

    // 删除回答
     void deleteAnswer(Long answerId, Long userId) ;

    // 点赞/踩
     void voteAnswer(Long answerId, Long userId, Integer voteType);

     void updateVoteCount(Long answerId);

    AnswerVO convertToVO(Answer answer, Long currentUserId);
}