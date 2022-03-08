package org.cxs.auth;

import lombok.extern.slf4j.Slf4j;
import org.cxs.auth.util.TokenDecode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@SpringBootApplication
@RestController
@RequestMapping("/demo")
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() {

        return new RestTemplate();
    }

    @GetMapping("/hello")
    public String hello(@RequestHeader("Authorization") String token) {
        TokenDecode tokenDecode = new TokenDecode();
        String atoken = token.split(" ")[1];
        Map<String, String> map = tokenDecode.dcodeToken(atoken);
        log.info("token json = {}", map);

        Map<String, String> userInfo = tokenDecode.getUserInfo();
        return "hello serviceA AAA, you token = " + token + "\n" + userInfo;
    }

    @GetMapping("/anyone")
    public String anyone() {
        return "hello anyone, you need't token";
    }

}
