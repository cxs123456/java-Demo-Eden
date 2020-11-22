package org.cxs.serviceA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
@RequestMapping("/demo")
public class DemoAserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoAserviceApplication.class, args);
    }

    @GetMapping("/hello")
    public String hello(@RequestHeader("Authorization") String token) {
        return "hello serviceA AAA, you token = " + token;
    }

    @GetMapping("/anyone")
    public String anyone() {
        return "hello anyone, you need't token";
    }
}
