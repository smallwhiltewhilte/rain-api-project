package com.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.mapper.TeachPlanMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.model.po.TeachPlan;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseCategoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ContentServiceApplicationTests {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Autowired
    CourseCategoryService courseCategoryService;
    @Autowired
    TeachPlanMapper teachPlanMapper;
    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Test
    void testTeachPlan() {
        //        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes("1-1");
//        System.out.println(courseCategoryTreeDtos);
//        List<TeachPlanDto> teachPlanDtos = teachPlanMapper.selectTreeNodes(18L);
//        System.out.println(teachPlanDtos);
//        LambdaQueryWrapper<TeachPlan> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(TeachPlan::getParentid,269);
//        List<TeachPlan> teachPlanList = teachPlanMapper.selectList(lambdaQueryWrapper);
//        System.out.println(teachPlanList);
//        lambdaQueryWrapper.eq(TeachPlan::getCourseId,25);
//        lambdaQueryWrapper.eq(TeachPlan::getGrade,1);
//        lambdaQueryWrapper.gt(TeachPlan::getId,12);
//        List<TeachPlan> teachPlans = teachPlanMapper.selectList(lambdaQueryWrapper);
//        System.out.println(teachPlans);
        LambdaQueryWrapper<CourseTeacher> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CourseTeacher::getCourseId,74);
        List<CourseTeacher> courseTeacherList = courseTeacherMapper.selectList(lambdaQueryWrapper);
        System.out.println(courseTeacherList);
    }

    @Test
    void testMapper() {
        CourseBase courseBase = courseBaseMapper.selectById(2L);
        Assertions.assertNotNull(courseBase);
    }

    @Test
    void testCourseBaseInfoService() {
//        PageParams pageParams = new PageParams();
//        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(pageParams, new QueryCourseParamsDto());
//        System.out.println(courseBasePageResult);
    }

    @Test
    void testCourseCategoryService() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes();
        System.out.println(courseCategoryTreeDtos);

    }
}
