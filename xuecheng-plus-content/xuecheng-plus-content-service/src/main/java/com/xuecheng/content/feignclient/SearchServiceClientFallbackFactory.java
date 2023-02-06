package com.xuecheng.content.feignclient;

import com.xuecheng.content.feignclient.model.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
/**
 * @author wangzan
 * @version 1.0
 * @description SearchServiceClient熔断降级处理类
 * @date 2023/1/30
 */
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                throwable.printStackTrace();
                log.debug("调用搜索服务上传文件时发生熔断，异常信息:{}",throwable.getMessage());

                return false;
            }
        };
    }
}
