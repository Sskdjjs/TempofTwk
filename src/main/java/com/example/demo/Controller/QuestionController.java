package com.example.demo.Controller;


import com.example.demo.DTO.request.CreateQuestionRequest;
import com.example.demo.DTO.request.QuestionQueryRequest;
import com.example.demo.DTO.request.UpdateQuestionRequest;
import com.example.demo.DTO.response.ApiResponse;
import com.example.demo.DTO.response.PageResult;
import com.example.demo.DTO.response.QuestionVO;
import com.example.demo.Service.QuestionService;
import com.example.demo.entity.Question;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
@Tag(name = "问题管理", description = "问题的增删改查等操作")
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 创建问题
     * POST http://localhost:8080/api/questions
     */
    @PostMapping
    @Operation(summary = "创建问题", description = "需要认证")
    public ApiResponse<QuestionVO> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request,
            @Parameter(hidden = true) @RequestAttribute Long userId) {

        log.info("创建问题请求: userId={}, title={}", userId, request.getTitle());

        try {
            QuestionVO questionVO = questionService.createQuestion(request, userId);
            return ApiResponse.success(questionVO);
            /**
             * 他打印了什么创建成功
             */
        } catch (Exception e) {
            log.error("创建问题失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取问题列表
     * GET http://localhost:8080/api/questions
     */
    @GetMapping
    @Operation(summary = "获取问题列表", description = "支持分页、搜索、排序")
    public ApiResponse<PageResult<QuestionVO>> getQuestions(
            @Valid QuestionQueryRequest request) {

        log.info("查询问题列表请求: {}", request);

        try {
            PageResult<QuestionVO> result = questionService.queryQuestions(request);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("查询问题列表失败: {}", e.getMessage());
            return ApiResponse.error("查询失败");
        }
    }

    /**
     * 获取问题详情
     * GET http://localhost:8080/api/questions/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取问题详情")
    public ApiResponse<QuestionVO> getQuestion(
            @PathVariable Long id) {

        log.info("获取问题详情请求: questionId={}", id);

        try {
            QuestionVO questionVO = questionService.getQuestionById(id);
            return ApiResponse.success(questionVO);
        } catch (Exception e) {
            log.error("获取问题详情失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新问题
     * PUT http://localhost:8080/api/questions/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新问题", description = "只能更新自己的问题")
    public ApiResponse<QuestionVO> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuestionRequest request,
            @Parameter(hidden = true) @RequestAttribute Long userId) {

        log.info("更新问题请求: questionId={}, userId={}", id, userId);

        try {
            QuestionVO questionVO = questionService.updateQuestion(id, request, userId);
            return ApiResponse.success( questionVO);
            /**
             * 打印更新成功
             */
        } catch (Exception e) {
            log.error("更新问题失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除问题
     * DELETE http://localhost:8080/api/questions/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除问题", description = "软删除，只能删除自己的问题")
    public ApiResponse<Boolean> deleteQuestion(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestAttribute Long userId) {

        log.info("删除问题请求: questionId={}, userId={}", id, userId);

        try {
            boolean success = questionService.deleteQuestion(id, userId);
            if (success) {
                return ApiResponse.success(success);
                /**打印删除成功
                 * 这个先不管能跑就行
                 */
            } else {
                return ApiResponse.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除问题失败: {}", e.getMessage());
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户的问题列表
     * GET http://localhost:8080/api/questions/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的问题列表")
    public ApiResponse<PageResult<Question>> getUserQuestions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("获取用户问题列表: userId={}", userId);

        try {
            PageResult<Question> result = questionService.getUserQuestions(userId, page, size);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("获取用户问题列表失败: {}", e.getMessage());
            return ApiResponse.error("查询失败");
        }
    }
}