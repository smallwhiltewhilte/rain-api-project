package com.xuecheng.learning.feignclient;

import com.xuecheng.content.model.po.CoursePublish;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangzan
 * @version 1.0
 * @description 内容管理服务远程接口
 * @date 2023/1/31
 */
@FeignClient(value = "content-api", fallbackFactory = ContentServiceClientFallbackFactory.class)
@RequestMapping("/content")
public interface ContentServiceClient {
    @GetMapping("/r/coursepublish/{courseId}")
    @ResponseBody
    CoursePublish getCoursepublish(@PathVariable("courseId") Long courseId);
}
