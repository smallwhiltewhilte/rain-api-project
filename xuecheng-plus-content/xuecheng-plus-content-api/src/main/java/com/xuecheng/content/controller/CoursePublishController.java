package com.xuecheng.content.controller;

import com.alibaba.fastjson.JSON;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.TeachPlan;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程预览发布接口
 * @date 2023/1/28
 */
@Api(value = "课程预览发布接口", tags = "课程预览发布接口")
@Controller
public class CoursePublishController {
    @Autowired
    CoursePublishService coursePublishService;

    @ApiOperation("课程预览")
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId) {
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model", coursePreviewInfo);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

    @ApiOperation("课程提交审核")
    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        Long companyId = Long.valueOf(user.getCompanyId());
        coursePublishService.commitAudit(companyId, courseId);
    }

    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping("/coursepublish/{courseId}")
    public void coursePublish(@PathVariable("courseId") Long courseId) {
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        Long companyId = Long.valueOf(user.getCompanyId());
        coursePublishService.coursePublish(courseId, companyId);
    }

    @ApiOperation("查询课程发布信息")
    @ResponseBody
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursepublish(@PathVariable("courseId") Long courseId) {
        return coursePublishService.getCoursePublish(courseId);
    }

    @ApiOperation("获取课程发布信息")
    @ResponseBody
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getCoursePublish(@PathVariable("courseId") Long courseId) {
        // 查询课程发布信息
        CoursePublish coursePublish = coursePublishService.getCoursePublishCache(courseId);
        if (coursePublish == null) {
            return new CoursePreviewDto();
        }
        // 封装基本信息、营销信息和师资信息
        CourseBaseInfoDto courseBase = new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish, courseBase);
        // 封装课程计划
        String teachplan = coursePublish.getTeachplan();
        List<TeachPlanDto> teachPlanDtos = JSON.parseArray(teachplan, TeachPlanDto.class);
        // 封装师资信息
        String teachers = coursePublish.getTeachers();
        List<CourseTeacherDto> courseTeacherDtos = JSON.parseArray(teachers, CourseTeacherDto.class);
        // 要封装的对象
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBase);
        coursePreviewDto.setTeachPlans(teachPlanDtos);
        coursePreviewDto.setCourseTeachers(courseTeacherDtos);
        return coursePreviewDto;
    }
}
