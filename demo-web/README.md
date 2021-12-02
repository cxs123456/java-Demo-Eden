# web应用案例干货demo

> 业务中常用的web案例

## 1.Springboot Shiro /Spring Security 应用



## 2.SpringBoot 项目i18n国际化错误码实现方案


## 3.log日志切面+注解


## 4.多数据源服务，默认数据源，可实现数据源切换，动态添加数据源
**方式1：**  
思路：`Spring 2.0.1`引入了 `AbstractRoutingDataSource` 抽象类，实现根据 `lookup key` 从多个数据源中获取目标数据源。  
源码如下：最主要的方法：`determineTargetDataSource()` 决定使用哪一个数据源，方法中调用了`determineCurrentLookupKey()`来获取当前数据源的 `lookup key`，所有该抽象类的实现类都要实现这个方法。

> 参考链接： 
> (https://blog.csdn.net/weixin_34216036/article/details/92187801)
> 
**方式2：**  
`dynamic-datasource-spring-boot-starter`

> 参考链接： 
> [基于mybatis plus实现数据源动态添加、删除、切换，自定义数据源](https://blog.csdn.net/qq_38721537/article/details/121434339)
> https://blog.csdn.net/itwxming/article/details/103927526
> [Spring JdbcTemplate方法详解](https://blog.csdn.net/dyllove98/article/details/7772463)  


## 5.canal数据同步，同步mysql数据库