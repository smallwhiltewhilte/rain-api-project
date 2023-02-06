package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 *
 * @author wangzan
 * @since 2023-01-16
 */
public interface CourseBaseInfoService {
    /**
     * @param params               分页参数
     * @param queryCourseParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.content.model.po.CourseBase>
     * @description 课程查询
     * @date 2023/1/16 12:20
     **/
    PageResult<CourseBase> queryCourseBaseList(Long companyId,PageParams params, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @param companyId    培训机构id
     * @param addCourseDto 新增课程基本信息
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @description 新增课程
     * @date 2023/1/17
     **/
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * @param courseId
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @description 根据id查询课程信息
     * @date 2023/1/19
     **/
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    /**
     * @param companyId     机构id
     * @param editCourseDto 修改课程基本信息
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @description 修改课程
     * @date 2023/1/17
     **/
    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);

    /**
     * @return void
     * @description 删除课程
     * @date 2023/1/28
     * @param    courseId    课程id
     **/
    void deleteCourseBase(Long courseId);
}
