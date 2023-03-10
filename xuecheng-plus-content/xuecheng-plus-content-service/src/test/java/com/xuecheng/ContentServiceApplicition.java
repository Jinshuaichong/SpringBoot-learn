package com.xuecheng;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {"com.xuecheng.content.feignclient"})
@SpringBootApplication
public class ContentServiceApplicition {

    public static void main(String[] args) {
        SpringApplication.run(ContentServiceApplicition.class, args);
    }

}
