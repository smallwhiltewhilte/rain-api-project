package com.xuecheng.learning.feignclient;

import com.xuecheng.content.model.po.CoursePublish;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description ContentServiceClient熔断降级处理
 * @date 2023/1/31
 */
@Slf4j
@Component
public class ContentServiceClientFallbackFactory implements FallbackFactory<ContentServiceClient> {
    @Override
    public ContentServiceClient create(Throwable throwable) {
        return new ContentServiceClient() {
            @Override
            public CoursePublish getCoursepublish(Long courseId) {
                log.error("远程调用内容管理服务熔断异常：{}",throwable.getMessage());
                throwable.printStackTrace();
                return new CoursePublish();
            }
        };
    }
}