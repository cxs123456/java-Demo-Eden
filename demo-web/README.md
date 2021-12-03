# web应用案例干货demo

> 业务中常用的web案例

## 1.Springboot Shiro /Spring Security 应用



## 2.SpringBoot 项目i18n国际化错误码实现方案


## 3.log日志切面+注解


## 4.mybatis-plus 多数据源服务
> 功能实现：使用 **方式2**
> 设置master默认数据源，可实现数据源切换，动态添加移除数据源，手动切换数据源，手动注入多数据源，自定义数据源来源  
，启动初始化执行脚本

**方式1：**  
思路：`Spring 2.0.1`引入了 `AbstractRoutingDataSource` 抽象类，实现根据 `lookup key` 从多个数据源中获取目标数据源。  
源码如下：最主要的方法：`determineTargetDataSource()` 决定使用哪一个数据源，方法中调用了`determineCurrentLookupKey()`来获取当前数据源的 `lookup key`，所有该抽象类的实现类都要实现这个方法。

> 参考链接： 
> (https://blog.csdn.net/weixin_34216036/article/details/92187801)

**方式2：**  
mybatis-plus多数据源 方案，引用`dynamic-datasource-spring-boot-starter`项目

> 参考链接： 
> [基于mybatis plus实现数据源动态添加、删除、切换，自定义数据源](https://blog.csdn.net/qq_38721537/article/details/121434339)
> https://blog.csdn.net/itwxming/article/details/103927526
> [Spring JdbcTemplate方法详解](https://blog.csdn.net/dyllove98/article/details/7772463)  

**mybatis-plus多数据源 实现原理**  
核心数据源切换工具类：`DynamicDataSourceContextHolder`，核心动态数据源组件：`DynamicRoutingDataSource`该类实现了'DataSource'接口装配到spring容器中，  
作为唯一数据源组件，`dataSourceMap`属性集成多个数据源连接池实现多数据源切换功能，内部数据源的创建类  `AbstractDataSourceCreator`的实现类负责创建各种集  
成的数据源连接池，默认阿里的druild数据源连接池。

> 整体逻辑原理很简单，废了10W我脑力才看懂 o(╯□╰)o 。

## 5.canal数据同步，同步mysql数据库