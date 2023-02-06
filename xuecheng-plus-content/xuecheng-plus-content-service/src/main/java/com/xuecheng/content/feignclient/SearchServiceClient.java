package com.xuecheng.content.feignclient;

import com.xuecheng.content.feignclient.model.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangzan
 * @version 1.0
 * @description 搜索服务远程接口
 * @date 2023/1/30
 */

@FeignClient(value = "search",  fallbackFactory = SearchServiceClientFallbackFactory.class)
@RequestMapping("/search")
public interface SearchServiceClient {

    @PostMapping("/index/course")
    Boolean add(@RequestBody CourseIndex courseIndex);
}
