package com.xuecheng.content.model.dto;

import lombok.Data;

/**
 * @author wangzan
 * @version 1.0
 * @description 修改课程dto
 * @date 2023/1/18
 */
@Data
public class EditCourseDto extends AddCourseDto{
    //课程id
    private Long id;
}
