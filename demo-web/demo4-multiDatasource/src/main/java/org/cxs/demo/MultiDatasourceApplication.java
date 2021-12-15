package org.cxs.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/1 19:49
 **/
@SpringBootApplication
@MapperScan({"org.cxs.demo.dao"})
public class MultiDatasourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiDatasourceApplication.class, args);
    }
}
