package com.xuecheng.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程-教师关系表 服务实现类
 * @date 2023/1/17
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Override
    public List<CourseTeacherDto> getCourseTeacher(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeacherList = courseTeacherMapper.selectList(lambdaQueryWrapper);
        List<CourseTeacherDto> courseTeacherDtos = new ArrayList<>();
        courseTeacherList.forEach(courseTeacher -> {
            CourseTeacherDto courseTeacherDto = new CourseTeacherDto();
            BeanUtils.copyProperties(courseTeacher,courseTeacherDto);
            courseTeacherDtos.add(courseTeacherDto);
        });
        return courseTeacherDtos;
    }


    @Override
    public CourseTeacherDto saveCourseTeacher(CourseTeacher courseTeacher) {
        CourseTeacherDto courseTeacherDto = new CourseTeacherDto();
        saveOrUpdate(courseTeacher);
        BeanUtils.copyProperties(courseTeacher, courseTeacherDto);
        return courseTeacherDto;
    }

    @Override
    public void deleteCourseTeacher(Long courseId, Long id) {
        LambdaQueryWrapper<CourseTeacher> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CourseTeacher::getId, id);
        lambdaQueryWrapper.eq(CourseTeacher::getCourseId, courseId);
        courseTeacherMapper.delete(lambdaQueryWrapper);
    }
}
