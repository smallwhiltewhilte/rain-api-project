package com.xuecheng.learning.service;

import com.xuecheng.base.model.PageResult;
import com.xuecheng.learning.model.dto.MyCourseTableItemDto;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;

/**
 * @author wangzan
 * @version 1.0
 * @description 我的课程表service接口
 * @date 2023/1/31
 */
public interface MyCourseTablesService {
    /**
     * @param userId   用户id
     * @param courseId 课程id
     * @return com.xuecheng.learning.model.dto.XcChooseCourseDto
     * @description 添加选课
     * @date 2023/1/31
     **/
    XcChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * @param userId
     * @param courseId
     * @return XcCourseTablesDto 学习资格状态 [{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
     * @description 判断学习资格
     * @date 2023/1/31
     */
    XcCourseTablesDto getLeanringStatus(String userId, Long courseId);

    /**
     * @param params
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.learning.model.dto.MyCourseTableItemDto>
     * @description 分页查询
     * @date 2023/2/2
     **/
    PageResult<MyCourseTableItemDto> mycourestabls(MyCourseTableParams params);

    /**
     * @param xcChooseCourse
     * @return com.xuecheng.learning.model.po.XcCourseTables
     * @description 添加我的课程表
     * @date 2023/2/2
     **/
    XcCourseTables addCourseTabls(XcChooseCourse xcChooseCourse);

    /**
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.learning.model.po.XcCourseTables>
     * @description 查询我的课程表
     * @date 2023/2/6
     * @param    params    查询参数
     **/
    PageResult<XcCourseTables> myCourseTable(MyCourseTableParams params);
}
