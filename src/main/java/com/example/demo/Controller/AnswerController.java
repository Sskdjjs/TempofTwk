package com.example.demo.Controller;

import com.example.demo.DTO.response.AnswerVO;
import com.example.demo.DTO.response.ApiResponse;
import com.example.demo.DTO.response.CreateAnswerDTO;
import com.example.demo.DTO.response.VoteDTO;
import com.example.demo.Service.AnswerService;
import com.example.demo.Service.UserService;
import com.example.demo.entity.Answer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;
    private final UserService userService;

    @PostMapping
    public ApiResponse<AnswerVO> createAnswer(@Valid @RequestBody CreateAnswerDTO dto,
                                              @RequestParam Long questionId,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
//        Long userId = userService.getCurrentUserId(request);
        Answer answer = answerService.createAnswer(questionId, userId, dto.getContent());
        return ApiResponse.success(convertToVO(answer, userId));
    }

    @GetMapping("/question/{questionId}")
    public ApiResponse<List<AnswerVO>> getAnswers(@PathVariable Long questionId,
                                                  HttpServletRequest request) {
        Long userId = userService.getCurrentUserId(request);
        List<AnswerVO> answers = answerService.getAnswersByQuestionId(questionId, userId);
        return ApiResponse.success(answers);
    }

    @DeleteMapping("/{answerId}")
    public ApiResponse<Void> deleteAnswer(@PathVariable Long answerId,
                                          HttpServletRequest request) {
        Long userId = userService.getCurrentUserId(request);
        answerService.deleteAnswer(answerId, userId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{answerId}/vote")
    public ApiResponse<Void> voteAnswer(@PathVariable Long answerId,
                                        @Valid @RequestBody VoteDTO dto,
                                        HttpServletRequest request) {
        Long userId = userService.getCurrentUserId(request);
        answerService.voteAnswer(answerId, userId, dto.getVoteType());
        return ApiResponse.success(null);
    }

    private AnswerVO convertToVO(Answer answer, Long currentUserId) {
        // 这里调用service的convert方法
        return answerService.convertToVO(answer, currentUserId);
    }
}