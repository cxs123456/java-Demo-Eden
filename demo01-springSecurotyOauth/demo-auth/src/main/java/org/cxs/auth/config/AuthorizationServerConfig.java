package org.cxs.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bootstrap.encrypt.KeyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.annotation.Resource;
import java.security.KeyPair;


@Configuration
@EnableAuthorizationServer
class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    //jwt令牌转换器
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    //SpringSecurity 用户自定义授权认证类
    @Autowired
    UserDetailsService userDetailsService;
    // spring Security配置 OAuth2 授权认证管理器
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder bCryptPasswordEncoder;

    //令牌持久化存储接口
    @Autowired
    TokenStore tokenStore;
    @Autowired
    private CustomUserAuthenticationConverter customUserAuthenticationConverter;

    /***
     * 客户端信息配置
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
                .authorizedGrantTypes(
                        "authorization_code",          //根据授权码生成令牌
                        "client_credentials",          //客户端认证
                        "refresh_token",                //刷新令牌
                        "password")                     //密码方式认证
                .scopes("app")                        //客户端范围，名称自定义，必填
                .resourceIds("app1")    //资源服务器id,需要与资源服务器对应

                .and() //
                .withClient("aaabbb")
                .secret(bCryptPasswordEncoder.encode("123123"))
                .authorizedGrantTypes("password")
                .scopes("app")
                .resourceIds("app2");

        // 暂时不是使用数据库
        // clients.jdbc(dataSource).clients(clientDetails());
        // 自定义客户端服务
        // clients.withClientDetails()
    }

    /***
     * 授权服务器端点配置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);// 认证管理器

        // 这里使用客户端jwt token，也可以内存存储token,也可以使用redis和数据库
        endpoints.tokenStore(tokenStore);//令牌存储

        endpoints.accessTokenConverter(jwtAccessTokenConverter);
        // endpoints.userDetailsService(userDetailsService);     //用户信息service

        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET,HttpMethod.POST);
        // 可以给token多添加数据
//        endpoints.tokenEnhancer(new TokenEnhancer() {
//            @Override
//            public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
//                //在返回token的时候可以加上一些自定义数据
//                DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) oAuth2AccessToken;
//                Map<String, Object> map = new LinkedHashMap<>();
//                map.put("nickname", "测试姓名");
//                token.setAdditionalInformation(map);
//                return token;
//            }
//        });

    }

    /***
     * 授权服务器的安全配置
     * @param oauthServer
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.allowFormAuthenticationForClients()
                .passwordEncoder(bCryptPasswordEncoder)
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }


    //读取密钥的配置
    @Bean("keyProp")
    public KeyProperties keyProperties() {
        return new KeyProperties();
    }

    @Resource(name = "keyProp")
    private KeyProperties keyProperties;

    //客户端配置
    /*@Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }*/

    @Bean
    @Autowired
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    /****
     * JWT令牌转换器，定义token的生成方式
     * @param customUserAuthenticationConverter
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(CustomUserAuthenticationConverter customUserAuthenticationConverter) {
        // JWT令牌转换器，定义token的生成方式
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyPair keyPair = new KeyStoreKeyFactory(
                keyProperties.getKeyStore().getLocation(),                          //证书路径 changgou.jks
                keyProperties.getKeyStore().getSecret().toCharArray())              //证书秘钥 changgouapp
                .getKeyPair(
                        keyProperties.getKeyStore().getAlias(),                     //证书别名 changgou
                        keyProperties.getKeyStore().getPassword().toCharArray());   //证书密码 changgou
        converter.setKeyPair(keyPair);
        //配置自定义的CustomUserAuthenticationConverter
        DefaultAccessTokenConverter accessTokenConverter = (DefaultAccessTokenConverter) converter.getAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(customUserAuthenticationConverter);
        return converter;
    }
}
