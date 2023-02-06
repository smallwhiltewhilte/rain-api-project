package com.xuecheng.content.controller;

import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 前端控制器
 * </p>
 *
 * @author wangzan
 */
@Api(value = "课程师资管理接口", tags = "课程师资管理接口")
@Slf4j
@RestController
@RequestMapping("/courseTeacher")
public class CourseTeacherController {
    @Autowired
    private CourseTeacherService  courseTeacherService;
    @ApiOperation(value = "查询课程教师")
    @GetMapping("/list/{courseId}")
    public List<CourseTeacherDto> getCourseTeacher(@PathVariable Long courseId){
        return courseTeacherService.getCourseTeacher(courseId);
    }

    @ApiOperation(value = "保存课程教师")
    @PostMapping
    public CourseTeacherDto saveCourseTeacher(@RequestBody CourseTeacher courseTeacher){
        return courseTeacherService.saveCourseTeacher(courseTeacher);
    }
    @ApiOperation(value = "删除课程教师")
    @DeleteMapping("/course/{courseId}/{id}")
    public void deleteCourseTeacher(@PathVariable("courseId") Long courseId,@PathVariable("id") Long id){
        courseTeacherService.deleteCourseTeacher(courseId,id);
    }

}
