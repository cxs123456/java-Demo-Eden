package org.cxs.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * note:
 *
 * @author cxs
 * @date 2021/12/2 14:23
 **/
@ConfigurationProperties(prefix = "spring.datasource.dynamic.datasource.master")
@Data
public class BaseDsProperties {

    /**
     * mybatis plus默认数据源 primary
     */
    private String primary = "master";
    private String driverClassName;
    private String url;
    private String username;
    private String password;

}
