package com.xuecheng.content.controller;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程分类查询接口
 * @date 2023/1/17
 */
@Slf4j
@Api("课程分类查询接口")
@RestController
public class CourseCategoryController {
    @Autowired
    CourseCategoryService courseCategoryService;
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto>queryTreeNodes() {
        return courseCategoryService.queryTreeNodes();
    }
}
