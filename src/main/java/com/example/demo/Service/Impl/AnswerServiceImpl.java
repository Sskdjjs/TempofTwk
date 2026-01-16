package com.example.demo.Service.Impl;

import com.example.demo.DTO.response.AnswerVO;
import com.example.demo.Service.AnswerService;
import com.example.demo.entity.Answer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    @Override
    public Answer createAnswer(Long questionId, Long userId, String content) {
        return null;
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
        return null;
    }
}
