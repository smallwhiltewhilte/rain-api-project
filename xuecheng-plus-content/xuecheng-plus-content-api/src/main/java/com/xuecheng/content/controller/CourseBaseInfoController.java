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
import com.xuecheng.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程基本信息接口
 * @date 2023/1/16
 */
@Api(value = "课程管理接口", tags = "课程管理接口")
@RestController
@RequestMapping("/course")
public class CourseBaseInfoController {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/list")
//    @PreAuthorize(("hasAuthority('course_find_list')"))
    public PageResult<CourseBase> list(PageParams params, @RequestBody QueryCourseParamsDto queryCourseParamsDto) {
        // 实现细粒度授权
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        // 机构id
        String companyId = user.getCompanyId();
        // 调用service获取数据
        return courseBaseInfoService.queryCourseBaseList(Long.parseLong(companyId),params, queryCourseParamsDto);
    }

    @ApiOperation("新增课程接口")
    @PostMapping
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated(ValidationGroups.Insert.class) AddCourseDto addCourseDto) {
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

    @ApiOperation("根据id查询课程信息")
    @GetMapping("/{courseId}")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable Long courseId) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        System.out.println(user);
        return courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    @ApiOperation("修改课程基础信息")
    @PutMapping
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated EditCourseDto editCourseDto) {
        Long companyId = 22L;
        return courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
    }

    @ApiOperation("修改课程基础信息")
    @DeleteMapping("/{courseId}")
    public void deleteCourseBase(@PathVariable Long courseId) {
        courseBaseInfoService.deleteCourseBase(courseId);
    }

}
