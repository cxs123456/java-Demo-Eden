# mybatis-plus 多数据源服务

## 多数据源相关配置

**1.pom文件中配置， mybatis-plus的多数据源依赖**  
``` xml
    <!--mybatis-plus-->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.4.3.4</version>
    </dependency>
     
    <!--dynamic-datasource-->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
        <version>3.4.1</version>
    </dependency>
```

**2.yaml文件中的配置**  
``` yaml
spring:
  datasource:
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #严格匹配数据源,默认false. true未匹配到指定数据源时抛异常,false使用默认数据源
      datasource:
        #指定多个数据源
        master:
          url: jdbc:mysql://127.0.0.1:3306/db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
          username: root
          password: 123456
          driver-class-name: com.mysql.cj.jdbc.Driver
        slave_1:
          url: jdbc:mysql://127.0.0.1:3306/db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
          username: root
          password: 123456
          driver-class-name: com.mysql.cj.jdbc.Driver
      druid:
        wall:
          comment-allow: true
          none-base-statement-allow: true
          variant-check: false
          multi-statement-allow: true
        #......省略
        #以上会配置一个默认库master，一个组slave下有两个子库slave_1,slave_2
      lazy: true # 懒加载数据源
```

**2.java 配置文件`org.cxs.demo.config.DsConfig`**  
该文件配置类中 自定义加载数据源方式，如果要求项目启动时候从数据库中加载数据源可以使用这种方式实现。

**3.多数据源使用示例 参考`org.cxs.demo.controller.DSController`**