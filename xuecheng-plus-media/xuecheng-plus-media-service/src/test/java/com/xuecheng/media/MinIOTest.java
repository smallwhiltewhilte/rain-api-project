package com.xuecheng.media;

import io.minio.*;
import io.minio.errors.MinioException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;

/**
 * @author wangzan
 * @version 1.0
 * @description 测试minio上传文件、删除文件、查询文件
 * @date 2023/1/25
 */
public class MinIOTest {
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://centos7:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    void upload() {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("testbucket")
                            .object("2.mp4")
                            .filename("D:\\xc_edu\\2.mp4")
                            .build());
            System.out.println("上传成功了");
        } catch (Exception e) {
            System.out.println("上传失败了");
        }
    }
    @Test
    void upload2() {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("testbucket")
                            .object("test/3.avi")
                            .filename("D:\\xc_edu\\3.avi")
                            .build());
            System.out.println("上传成功了");
        } catch (Exception e) {
            System.out.println("上传失败了");
        }
    }
    @Test
    void delete() {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket("testbucket").object("test/3.avi").build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
        }
    }
    @Test
    void getFile() {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("2.mp4").build();
        try (
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
                FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\xc_edu\\2_1.mp4"));
        ) {
            if (inputStream != null) {
                IOUtils.copy(inputStream, fileOutputStream);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
