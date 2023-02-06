package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseTeacher;
import lombok.Data;

import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程预览dto
 * @date 2023/1/28
 */
@Data
public class CoursePreviewDto {

    /**
     * 课程基本信息,课程营销信息
     */
    private CourseBaseInfoDto courseBase;
    /**
     * 课程计划信息
     */
    private List<TeachPlanDto> teachPlans;
    /**
     * 师资信息
     */
    private List<CourseTeacherDto> courseTeachers;
}
