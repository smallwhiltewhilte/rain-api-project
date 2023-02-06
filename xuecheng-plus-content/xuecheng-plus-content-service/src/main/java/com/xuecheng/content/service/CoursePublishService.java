package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.po.CoursePublish;

import java.io.File;

/**
 * <p>
 * 课程预览发布 服务类
 * </p>
 *
 * @author wangzan
 */
public interface CoursePublishService {
    /**
     * @param courseId 课程id
     * @return com.xuecheng.content.model.dto.CoursePreviewDto
     * @description 获取课程预览信息
     * @date 2023/1/28
     **/
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * @param companyId 机构id
     * @param courseId  课程id
     * @return void
     * @description 提交审核
     * @date 2023/1/29
     **/
    public void commitAudit(Long companyId, Long courseId);

    /**
     * @param courseId  课程id
     * @param companyId 机构id
     * @return void
     * @description 课程发布
     * @date 2023/1/29
     **/
    void coursePublish(Long courseId, Long companyId);

    /**
     * @param courseId 课程id
     * @return java.io.File
     * @description 生成静态页面
     * @date 2023/1/29
     **/
    File generateCourseHtml(Long courseId);

    /**
     * @param courseId 课程id
     * @param file
     * @return void
     * @description 上传课程静态页面
     * @date 2023/1/29
     **/
    void uploadCourseHtml(Long courseId, File file);

    /**
     * @param courseId 课程id
     * @return java.lang.Boolean
     * @description 创建索引
     * @date 2023/1/30
     **/
    Boolean saveCourseIndex(Long courseId);

    /**
     * @param courseId 课程id
     * @return com.xuecheng.content.model.po.CoursePublish
     * @description 获取已发布课程
     * @date 2023/1/31
     **/
    CoursePublish getCoursePublish(Long courseId);

    /**
     * @return com.xuecheng.content.model.po.CoursePublish
     * @description
     * @date 2023/1/30
     * @param    courseId
     **/
    CoursePublish getCoursePublishCache(Long courseId);

    /**
     * @return java.lang.Boolean
     * @description
     * @date 2023/1/30
     * @param    courseId
     **/
    Boolean saveCourseCache(Long courseId);
}
