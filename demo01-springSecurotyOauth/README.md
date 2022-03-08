# SpringSecurity + OAuth2 + JWT 实现微服务认证鉴权

## 一、SpringSecurity

Spring Security是一套安全框架，可以基于RBAC（基于角色的权限访问控制）对用户的访问权限进行控制，核心思想是通过一系列的过滤器链`filter chain`来进行拦截过滤。  
最核心安全过滤器`FilterSecurityInterceptor`，通过`FilterInvocationSecurityMetadataSource`来进行资源权限的匹配，`AccessDecisionManager`来执行访问策略。

## 二、OAuth2

OAuth2是一个关于授权的开放标准，认证用户身份，并颁发token（令牌），使得第三方应用可以使用该token（令牌）在限定时间、限定范围访问指定资源。  
OAuth2服务器分为2种：认证授权服务器和资源服务器。

### OAuth2相关概念、4种角色

1. 资源所有者（`resources owner`）：拥有被访问资源的用户。
2. 客户端/第三方应用（`client`）：第三方应用，获取资源服务器提供的资源。
3. 授权服务器（`authorization server`）：认证服务器，提供授权许可code、令牌token等。
4. 资源服务器（`resource server`）：资源服务器，拥有被访问资源的服务器，需要通过token来确定是否有权限访问。

### OAuth2几种模式

获取令牌的方式主要有四种，分别是**授权码模式、密码模式、隐式授权码模式（简单模式）、和客户端模式**。
> 参考：  
> [阮一峰老师的理解OAuth 2.0](http://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html)  

## 三、JWT

JWT全称为Json Web Token，jwt格式的token分为三部分：  

- 头部`Header` ：jwt的头部承载2部分信息，声明类型和声明加密的算法，比如：`{'typ': 'JWT','alg': 'HS256'}`
- 载荷`Payload` ：载荷就是存放有效信息的地方。
- 签名`Signature` ：签证信息，`base64加密后的header`和`base64加密后的payload`通过`.`连接组成的字符串，然后通过header中声明的加密方式进行加盐`secret`(<font color='#FF3300'>保存在服务端的秘钥</font>)组合加密，然后就构成了jwt的第三部分。

> JWT是由这 3 段信息构成的，将这3段信息文本用`.`链接一起就构成了Jwt字符串，例如：  
`eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ`

## 有JWT为什么还要用OAuth2.0来做登入和权限认证

OAUTH2.0是一种授权方式，一种流程规范。  
JWT，是TOKEN的一种数据格式(服务端和客户端进行通信的传递安全可靠的信息的数据格式)。
> 参考：
> [深入理解Spring Cloud Security OAuth2及JWT](https://www.jianshu.com/p/cb886f995e86?utm_source=oschina-app)

## 异常问题

### 1. UserDetailsServiceImpl.loadUserByUsername(name) 被调用2次

**问题描述：**

1. 客户端 post 请求 `/auth/login` 接口，再服务内部RestTemplate调用 `/oauth/token`，`UserDetailsService.loadUserByUsername(name)` 方法会被调用2次，第1次调用传参 name 为 clientId，第2次传参 name 为 客户端输入的 username。
2. 如果客户端直接请求 `/oauth/token` 接口（按照oauth2的密码模式传参），`loadUserByUsername(name)`也会调用2次，  
重启应用时，第1次调用传参 name 为 客户端client_id，再次调用的是 username。


> 原因： 
> 主要是2个配置文件(`WebSecurityConfig`和`AuthorizationServerConfig`)的加载顺序，去掉 AuthorizationServerConfig 的`@Order(-1)`注解;
> AuthorizationServerConfig最先配置，AuthorizationServerConfig 必须最后注入到spring容器中
> 认证服务 想要访问本服务其他api，也需要配置 资源服务ResourceServerConfig 
> /oauth/token 返回的 json数据中，添加自定义数据？ 实现 new TokenEnhancer()，这种方式还会在返回的access_token jwt中 添加数据
> /oauth/token 返回的 json数据中 access_token jwt中 添加自定义数据 ？ 实现 DefaultUserAuthenticationConverter类复写 convertUserAuthentication方法 
> zzz

## 知识拓展

basic 认证和 bearer 授权访问 请求头 `Authorization` 对应不同值。

### 1.basic 认证和 bearer 授权

basic 认证：`Authorization:Basic Base64(username:password)`  
bearer 授权：`Authorization:Bearer token`

## SpringSecurity + OAuth2 + JWT 整合

### 1.SpringSecurity 的`WebSecurityConfigurerAdapter`配置

1.创建类继承 `WebSecurityConfigurerAdapter` 覆写 3 个 configure 配置方法，覆写 1 个 authenticationManagerBean方法并且注解 `@bean` 让spring管理。
2.配置`UserDetailsService`

### 2.OAuth2 的认证服务器`AuthorizationServerConfigurerAdapter`配置

1.创建类继承`AuthorizationServerConfigurerAdapter` 覆写 3 个 configure 配置方法
2.配置`ClientDetailsService`

### 3.OAuth2 的资源服务器`ResourceServerConfigurerAdapter`配置

1.创建类继承`ResourceServerConfigurerAdapter` 覆写 2 个 configure 配置方法，配置`JwtTokenStore`、`JwtAccessTokenConverter`2个类(用于访问jwt)。  

> 引用  
> [基于 Spring Security Oauth2 的 SSO 单点登录+JWT 权限控制实践](https://blog.csdn.net/Vermont_/article/details/116422782)  
> [spring官网Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)  
> [spring官网Spring Security OAuth](https://spring.io/projects/spring-security-oauth#learn)  
> [github文档](https://github.com/spring-projects/spring-security/wiki/OAuth-2.0-Migration-Guide)  


### 总结
**授权码模式示例：微信认证的流程**  
① 客户端（我们的web服务器）请求第三方授权，登录页面中微信/QQ图标  
② 资源拥有者（微信用户）同意给客户端授权，客户端得到授权码   
③ 客户端通过授权码，请求认证服务器申请令牌  
④ 认证服务器向客户端响应令牌，认证服务器验证客户端请求的授权码，如果合法则向客户端签发令牌   
⑤ 客户端携带令牌访问资源服务器的资源   
⑥ 资源服务器返回资源，资源服务器校验令牌的合法性，如果合法则向用户响应资源信息内容。  

**密码模式认证流程**  
① 客户端（我们的web服务器）登录页面，要求 资源拥有者 （我们的用户）输入密码和用户名，进行登录  
② 登录接口的url 默认是`/oauth/token`（无需自己开发，前端写死），该请求是向认证服务器申请令牌，按照密码模式的请求数据，   
重点包含2个密码，1个是请求体的用户的 `username和password`，1个是请求头的 `http Basic认证 ` 中客户端应用的`clientId/clientSecret`。  
④⑤⑥ 和授权码模式的业务逻辑一致。token的生成有`username和password`参与。返回的令牌由前端放入请求头cookie中下次请求。  