package com.xuecheng.content.controller;

import com.xuecheng.base.exception.ValidationGroups;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程基本信息接口
 * @date 2023/1/16
 */
@Api(value = "课程管理接口",tags = "课程管理接口")
@RestController
public class CourseBaseInfoController {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
  public PageResult<CourseBase> list(PageParams params, @RequestBody QueryCourseParamsDto queryCourseParamsDto){
       // 调用service获取数据
        return courseBaseInfoService.queryCourseBaseList(params, queryCourseParamsDto);
    }
    @ApiOperation("新增课程接口")
    @PostMapping("/course")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Insert.class) AddCourseDto addCourseDto){
        // 获取当前用户培训机构id
        Long companyId = 22L;
        // 调用service
        return courseBaseInfoService.createCourseBase(companyId, addCourseDto);
    }
    //测试分组校验
//    @PostMapping("/course2")
//    public CourseBaseInfoDto createCourseBase2(@RequestBody @Validated(ValidationGroups.Update.class) AddCourseDto addCourseDto){
//        // 获取当前用户培训机构id
//        Long companyId = 22L;
//        // 调用service
//        CourseBaseInfoDto courseBase = courseBaseInfoService.createCourseBase(companyId, addCourseDto);
//        return courseBase;
//    }
    @ApiOperation("修改课程基础信息")
    @GetMapping("/course/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId){

        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("修改课程基础信息")
    @PutMapping("/course")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated EditCourseDto editCourseDto){
        Long companyId = 22L;
        return courseBaseInfoService.updateCourseBase(companyId,editCourseDto);
    }
}
