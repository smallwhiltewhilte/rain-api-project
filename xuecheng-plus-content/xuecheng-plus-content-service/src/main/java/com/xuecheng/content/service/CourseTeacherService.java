package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务类
 * </p>
 *
 * @author wangzan
 */
public interface CourseTeacherService extends IService<CourseTeacher> {
    /**
     * @param courseId 课程id
     * @return java.util.List<com.xuecheng.content.model.po.CourseTeacher>
     * @description 查询课程教师
     * @date 2023/1/28
     **/
    List<CourseTeacherDto> getCourseTeacher(Long courseId);


    /**
     * @param courseTeacher
     * @return com.xuecheng.content.model.po.CourseTeacher
     * @description 新增或修改课程教师
     * @date 2023/1/28
     **/
    CourseTeacherDto saveCourseTeacher(CourseTeacher courseTeacher);

    /**
     * @return void
     * @description 删除课程教师
     * @date 2023/1/28
     * @param    courseId 课程id
     * @param    id    教师id
     **/
    void deleteCourseTeacher(Long courseId, Long id);
}
