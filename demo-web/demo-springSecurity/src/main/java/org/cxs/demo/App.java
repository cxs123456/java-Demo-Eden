package org.cxs.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * note:
 *
 * @author Administrator
 * @date 2021/11/25 17:00
 **/
@SpringBootApplication
@RestController
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }
}
