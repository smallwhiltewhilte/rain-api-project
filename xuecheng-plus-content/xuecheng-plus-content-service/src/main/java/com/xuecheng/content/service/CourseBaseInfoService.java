package com.xuecheng.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 *
 * @author wangzan
 * @since 2023-01-16
 */
public interface CourseBaseInfoService{
    /**
     * @description 课程查询
     * @date 2023/1/16 12:20
     * @param	params 分页参数
     * @param	queryCourseParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.content.model.po.CourseBase>
     **/
    public PageResult<CourseBase>  queryCourseBaseList(PageParams params, QueryCourseParamsDto queryCourseParamsDto);
}
