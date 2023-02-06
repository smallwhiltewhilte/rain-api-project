package com.xuecheng.content.feignclient;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author wangzan
 * @version 1.0
 * @description MediaServiceClient降级方法
 * @date 2023/1/29
 */
public class MediaServiceClientFallback implements MediaServiceClient{
    @Override
    public String uploadFile(MultipartFile upload, String folder, String objectName) {
        // 降级方法

        return null;
    }
}
