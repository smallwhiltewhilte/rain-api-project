package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程分类dto
 * @date 2023/1/17
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory {
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
