package com.xuecheng.content.model.dto;

import lombok.Data;

/**
 * @author wangzan
 * @version 1.0
 */
@Data
public class QueryCourseParamsDto {

    //审核状态
    private String auditStatus;
    //课程名称
    private String courseName;
    //发布状态
    private String publishStatus;
}
