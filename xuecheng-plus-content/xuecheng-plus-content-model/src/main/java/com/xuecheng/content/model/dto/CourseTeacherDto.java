package com.xuecheng.content.model.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.xuecheng.content.model.po.CourseTeacher;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程教师dto
 * @date 2023/1/28
 */
@Data
@ToString
public class CourseTeacherDto extends CourseTeacher {
}
