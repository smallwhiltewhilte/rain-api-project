package com.xuecheng.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 *     系统管理启动类
 * </p>
 *
 * @Description:
 */
@SpringBootApplication
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class,args);
        System.setProperty("spring.cloud.bootstrap.enabled","true");
    }
}