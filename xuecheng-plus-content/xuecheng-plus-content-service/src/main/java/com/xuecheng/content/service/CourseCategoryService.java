package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * <p>
 * 课程分类信息 服务类
 * </p>
 *
 * @author wangzan
 */
public interface CourseCategoryService {
    /**
     * @description 课程分类查询
     * @date 2023/1/17 12:06
     * @return java.util.List<com.xuecheng.content.model.dto.CourseCategoryTreeDto>
     **/
    List<CourseCategoryTreeDto> queryTreeNodes();
}
