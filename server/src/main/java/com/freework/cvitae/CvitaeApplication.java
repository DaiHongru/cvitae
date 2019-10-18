package com.freework.cvitae;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author daihongru
 */
@ComponentScan(basePackages = "com.freework")
@SpringCloudApplication
@EnableHystrixDashboard
public class CvitaeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CvitaeApplication.class, args);
    }

}
