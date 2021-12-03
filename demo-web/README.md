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
> (https://www.cnblogs.com/guishenyouhuo/articles/9956099.html)

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
**数据库同步方案：**  
1. 定时任务同步  
    定时任务技术有：SpringTask，Quartz，XXLJOB。  
    实现方法：第一次执行定时任务时，从MySQL数据库中以时间字段进行倒序排列查询相应的数据，并记录当前查询数据的时间字段的最大值，以后每次执行定时任务查询数据的时候，只要按时间字段倒序查询数据表中的时间字段大于上次记录的时间值的数据，并且记录本次任务查询出的时间字段的最大值即可，从而不需要再次查询数据表中的所有数据。  
    注意：这里所说的时间字段指的是标识数据更新的时间字段，也就是说，使用定时任务同步数据时，为了避免每次执行任务都会进行全表扫描，最好是在数据表中增加一个更新记录的时间字段。 
    优点：同步ES索引库的操作与业务代码完全解耦。缺点：数据的实时性并不高。
2. 消息队列同步  
    优点：业务代码解耦，并且能够做到准实时，目前tk的ES同步用的就是这种方式。缺点：需要在业务代码中加入发送消息到MQ的代码，数据调用接口耦合。
3. 通过CDC实现实时同步  
    CDC（change data capture，数据变更抓取），通过CDC来解析数据库的日志信息，来检测数据库中表结构和数据的变化，从而更新ES索引库，可以做到业务代码完全解耦，API完全解耦，可以做到准实时。  
	它的问题在于各种数据源的变更抓取没有统一的协议，如MySQL 用 Binlog，PostgreSQL 用 Logical decoding 机制，MongoDB 里则是 oplog。  
	【cdc工具】  
	- Canal，阿里开源的基于数据库增量日志解析，提供增量数据订阅&消费，目前主要支持了mysql。
	- Databus，Linkedin 的分布式数据变更抓取系统。
		它的 MySQL 变更抓取模块很不成熟，官方支持的是 Oracle，MySQL 只是使用另一个开源组件 OpenReplicator 做了一个 demo。另一个不利因素 databus 使用了自己实现的一个 Relay 作为变更分发平台，相比于使用开源消息队列的方案，这对维护和外部集成都不友好。
	- Mysql-Streamer，Yelp 的基于python的数据管道。
	- Debezium，Redhat 开源的数据变更抓取组件。
		支持 MySQL、MongoDB、PostgreSQL 三种数据源的变更抓取。Snapshot Mode 可以将表中的现有数据全部导入 Kafka，并且全量数据与增量数据形式一致，可以统一处理，很适合数据库迁移；	

**参考canal文档开发案例**


**本地测试遇到的巨坑**  
本地window环境按照mysql5.6+版本配置canal用户名、密码、权限，其中权限**需要多配置2个本地权限**，步骤如下:  
1. 创建用户名和密码  
`CREATE USER canal IDENTIFIED BY 'canal'; `  
2. 配置canal用户所有地址权限，但是这种配置不包括本地权限:      
`GRANT ALL PRIVILEGES ON *.* TO 'canal'@'%';`  
3. 配置canal用户本地地址权限，即localhost的权限，可以测试本地：
`GRANT ALL PRIVILEGES ON *.* TO 'canal'@'localhost';`  
`GRANT ALL PRIVILEGES ON *.* TO 'canal'@'127.0.0.1';`  
4. 刷新权限  `FLUSH PRIVILEGES;`

**重点：参考文档安装canal的ClientAdapter工程，提供了同步ES的客户端适配器工程**  
[canal-adapter](https://github.com/alibaba/canal/wiki/ClientAdapter)

> 参考链接  
> [GitHub上canal项目地址](https://github.com/alibaba/canal)，开发就参考GitHub上项目文档  
> [基于Canal的数据同步](https://www.cnblogs.com/xuxiaojian/p/14408862.html)  
> [Windows安装canal详细步骤](https://blog.csdn.net/u014386444/article/details/105874639)  
> https://blog.csdn.net/yehongzhi1994/article/details/107880162















