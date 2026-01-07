package com.example.demo.Service;


import com.example.demo.DTO.response.TagVO;

import java.util.List;

public interface TagService {

    // 创建或获取标签
    List<TagVO> processTags(List<String> tagNames, Long questionId);

    // 批量处理标签
    List<TagVO> getTagsByQuestionId(Long questionId);

    // 获取热门标签
    List<TagVO> getHotTags(int limit) ;

    // 搜索标签
    List<TagVO> searchTags(String keyword);

    void removeQuestionTags(Long questionId);
}