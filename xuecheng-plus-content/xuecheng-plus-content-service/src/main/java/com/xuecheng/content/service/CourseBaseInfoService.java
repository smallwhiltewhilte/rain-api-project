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
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.content.model.po.CourseBase>
     * @description 课程查询
     * @date 2023/1/16 12:20
     * @param    params 分页参数
     * @param    queryCourseParamsDto 查询条件
     **/
     PageResult<CourseBase> queryCourseBaseList(PageParams params, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     * @description 新增课程
     * @date 2023/1/17 14:51
     * @param    companyId 培训机构id
     * @param    addCourseDto 新增课程基本信息
     **/
     CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * @description 根据id查询课程信息
     * @date 2023/1/19 9:03
     * @param	courseId
     * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
     **/
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto);
}
