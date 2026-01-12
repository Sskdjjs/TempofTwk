package com.example.demo.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页响应对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    private List<T> list;       // 当前页数据列表
    private Long current;       // 当前页码
    private Long size;          // 每页大小
    private Long total;         // 总记录数
//    private Long pages;         // 总页数（可选）
//    // 添加4个参数的构造方法（必须添加！）
//    public PageResult(List<T> list, Long current, Long size, Long total) {
//        this.list = list;
//        this.current = current;
//        this.size = size;
//        this.total = total;
//    }

    // 计算总页数的方法
    public Long getPages() {
        if (size == null || size == 0) return 0L;
        return (total + size - 1) / size;  // 向上取整
    }
}