package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wangzan
 * @version 1.0
 * @description MediaServiceClient降级方法
 * @date 2023/1/29
 */
@Component
@Slf4j
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    @Override
    public MediaServiceClient create(Throwable throwable) {

        return new MediaServiceClient() {
            @Override
            public String uploadFile(MultipartFile upload, String folder, String objectName) {
                // 降级方法
                log.debug("调用媒资管理服务上传文件时发生熔断，异常信息:{}",throwable.getMessage());
                return null;
            }
        };
    }
}
