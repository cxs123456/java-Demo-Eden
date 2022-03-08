package org.cxs.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.util.stream.Collectors;


@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    //公钥
    private static final String PUBLIC_KEY = "public.key";
    //SpringSecurity 用户自定义授权认证类
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private UserDetailsService userDetailsService;
    // spring Security配置 OAuth2 授权认证管理器
    @Autowired
    private AuthenticationManager authenticationManager;
    // @Autowired
    // private CustomUserAuthenticationConverter customUserAuthenticationConverter;
    // @Autowired
    // private AuthorizationServerTokenServices authorizationServerTokenServices;
    /***
     * 客户端信息配置，指定客户端登录信息来源
     * 为了测试客户端与凭证存储在内存(生产应该用数据库来存储,oauth有标准数据库模板)
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 使用内存存储2个客户端
        clients.inMemory()
                .withClient("changgou")          //客户端id
                .secret("123456")                      //秘钥
                .redirectUris("http://localhost")       //重定向地址
                .accessTokenValiditySeconds(6000)          //访问令牌有效期
                .refreshTokenValiditySeconds(9000)         //刷新令牌有效期
                // 该client允许的授权类型 authorization_code,password,refresh_token,implicit,client_credentials
                .authorizedGrantTypes(
                        "authorization_code",
                        "client_credentials",
                        "refresh_token",
                        "password")
                // 客户端范围，名称自定义，必填
                .scopes("app")
                .resourceIds("app1")    //资源服务器id,需要与资源服务器对应
                .autoApprove(true)  //自动确认授权
                .and() //
                .withClient("web")
                .secret(passwordEncoder.encode("123456"))
                //.secret("123456")
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("all")
                .redirectUris("http://localhost")       //重定向地址
                .resourceIds("app2")
                .accessTokenValiditySeconds(6000)          //访问令牌有效期
                .refreshTokenValiditySeconds(9000)         //刷新令牌有效期
                //自动确认授权
                .autoApprove(true)
                .and().build();

        // 暂时不是使用数据库
        // clients.jdbc(dataSource).clients(clientDetails());
        // 自定义客户端数据服务
        // clients.withClientDetails(clientDetailsService())
    }

    /***
     * 授权服务器端点配置，OAuth2的主配置信息
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 密码模式必须配置，使用spring security 认证管理器
        endpoints.authenticationManager(authenticationManager);


        // 这里使用客户端jwt token，也可以内存存储token,也可以使用redis和数据库
        endpoints.tokenStore(tokenStore());//令牌存储
        endpoints.tokenServices(authorizationServerTokenServices());
        endpoints.accessTokenConverter(jwtAccessTokenConverter());
        // 密码模式必须配置，用户信息service
        // endpoints.userDetailsService(userDetailsService);
        // endpoints.setClientDetailsService(clientDetailsService);
        endpoints.userApprovalHandler(userApprovalHandler());
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
        // 可以给token多添加数据
        // endpoints.tokenEnhancer(new TokenEnhancer() {
        //     @Override
        //     public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        //         //在返回token的时候可以加上一些自定义数据
        //         UserJwt userJwt = (UserJwt) oAuth2Authentication.getPrincipal();// 获取用户信息
        //         DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) oAuth2AccessToken;
        //         Map<String, Object> map = new LinkedHashMap<>();
        //         map.put("nickname", userJwt.getId());
        //         map.put("id", userJwt.getId());
        //         map.put("phone", userJwt.getPhone());
        //         token.setAdditionalInformation(map);
        //         return token;
        //     }
        // });

    }


    /***
     * 授权服务器的安全配置，比如http 访问控制
     * @param oauthServer
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.allowFormAuthenticationForClients() // 允许客户端表单认证
                .passwordEncoder(passwordEncoder)
                // 获取token请求不进行拦截
                .tokenKeyAccess("permitAll()")
                // 通过验证返回token信息
                .checkTokenAccess("isAuthenticated()");
        // oauthServer.and().csrf().disable()
        //         // 启用Http基本身份验证
        //         .httpBasic()
        //         .and()
        //         // 启用表单身份验证
        //         .formLogin()
        //         .and()
        //         .authorizeRequests()    //限制基于Request请求访问
        //         .anyRequest()
        //         .authenticated();
    }


    //读取密钥的配置
    @Bean("keyProp")
    public KeyProperties keyProperties() {
        return new KeyProperties();
    }
    // 客户端配置
    /*@Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }*/

    /* //从数据库中查询出客户端信息
    @Bean
    public JdbcClientDetailsService clientDetailsService() {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
        return jdbcClientDetailsService;
    }*/

    @Bean
    public UserApprovalHandler userApprovalHandler() {
        ApprovalStoreUserApprovalHandler userApprovalHandler = new ApprovalStoreUserApprovalHandler();
        userApprovalHandler.setApprovalStore(approvalStore());
        userApprovalHandler.setClientDetailsService(this.clientDetailsService);
        userApprovalHandler.setRequestFactory(new DefaultOAuth2RequestFactory(this.clientDetailsService));
        return userApprovalHandler;
    }

    @Bean
    public ApprovalStore approvalStore() {
        TokenApprovalStore approvalStore = new TokenApprovalStore();
        approvalStore.setTokenStore(tokenStore());
        return approvalStore;
    }

    /**
     * 指定token的持久化策略其下有:
     * RedisTokenStore保存到redis中，
     * JdbcTokenStore保存到数据库中，
     * InMemoryTokenStore保存到内存中等实现类，
     * JwtTokenStore 保存jwt客户端
     *
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * JWT令牌转换器，定义token的生成方式
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        // JWT令牌转换器，定义token的生成方式
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyPair keyPair = new KeyStoreKeyFactory(
                keyProperties().getKeyStore().getLocation(),                          //证书路径 changgou.jks
                keyProperties().getKeyStore().getSecret().toCharArray())              //证书秘钥 changgouapp
                .getKeyPair(
                        keyProperties().getKeyStore().getAlias(),                     //证书别名 changgou
                        keyProperties().getKeyStore().getPassword().toCharArray());   //证书密码 changgou
        // 对称加密算法 RSA，设置jwt密钥
        converter.setKeyPair(keyPair);
        // 对称加密算法 HMAC，设置jwt密钥
        // converter.setSigningKey("123456");
        // converter.setVerifier(new MacSigner("123456"));
        //配置自定义的CustomUserAuthenticationConverter
        // DefaultAccessTokenConverter accessTokenConverter = (DefaultAccessTokenConverter) converter.getAccessTokenConverter();
        // accessTokenConverter.setUserTokenConverter(customUserAuthenticationConverter);
        return converter;
    }

    /**
     * 该方法用户获取一个token服务对象（该对象描述了token有效期等信息）
     */
    @Bean
    public AuthorizationServerTokenServices authorizationServerTokenServices() {
        // 使用默认实现
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setSupportRefreshToken(true); // 是否开启令牌刷新
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        // 针对jwt令牌的添加
        defaultTokenServices.setTokenEnhancer(jwtAccessTokenConverter());

        // 设置令牌有效时间（一般设置为2个小时）
        defaultTokenServices.setAccessTokenValiditySeconds(2 * 60 * 60); // access_token就是我们请求资源需要携带的令牌
        // 设置刷新令牌的有效时间
        defaultTokenServices.setRefreshTokenValiditySeconds(259200); // 3天

        return defaultTokenServices;
    }

    /**
     * 获取非对称加密公钥 Key
     *
     * @return 公钥 Key
     */
    private String getPubKey() {
        org.springframework.core.io.Resource resource = new ClassPathResource(PUBLIC_KEY);
        InputStreamReader inputStreamReader = null;
        BufferedReader br = null;
        try {
            inputStreamReader = new InputStreamReader(resource.getInputStream());
            br = new BufferedReader(inputStreamReader);
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
