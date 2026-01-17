package com.example.demo.Service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.demo.DTO.response.AnswerVO;
import com.example.demo.Service.AnswerService;
import com.example.demo.entity.Answer;
import com.example.demo.entity.Question;
import com.example.demo.mapper.AnswerMapper;
import com.example.demo.mapper.QuestionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerMapper answerMapper;
    private final QuestionMapper questionMapper;
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

    @Override
    public List<AnswerVO> getAnswersByQuestionId(Long questionId, Long currentUserId) {
        return null;
    }

    @Override
    public void deleteAnswer(Long answerId, Long userId) {

    }

    @Override
    public void voteAnswer(Long answerId, Long userId, Integer voteType) {

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
