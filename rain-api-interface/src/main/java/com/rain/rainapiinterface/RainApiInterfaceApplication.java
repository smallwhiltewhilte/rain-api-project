package com.rain.rainapiinterface;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rain.rainapiinterface.mapper")
public class RainApiInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RainApiInterfaceApplication.class, args);
    }

}
