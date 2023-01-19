package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程分类操作相关service
 * @date 2023/1/17
 */
public interface CourseCategoryService {
    /**
     * @description 课程分类查询
     * @date 2023/1/17 12:06
     * @return java.util.List<com.xuecheng.content.model.dto.CourseCategoryTreeDto>
     **/
    List<CourseCategoryTreeDto> queryTreeNodes();
}
