package org.cxs.auth.config;

import org.cxs.auth.filter.TokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableResourceServer
// //开启方法上的PreAuthorize注解
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    //公钥
    private static final String PUBLIC_KEY = "public.key";

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    // /***
    //  * 定义JwtTokenStore
    //  * @param jwtAccessTokenConverter
    //  * @return
    //  */
    // @Bean
    // public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
    //     return new JwtTokenStore(jwtAccessTokenConverter);
    // }
    //
    // /***
    //  * 定义JJwtAccessTokenConverter
    //  * @return
    //  */
    // @Bean
    // public JwtAccessTokenConverter jwtAccessTokenConverter() {
    //     JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    //     converter.setVerifierKey(getPubKey());
    //     return converter;
    // }

    /***
     * Http安全配置，对每个到达系统的http请求链接进行校验
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        //所有请求必须认证通过
        http.authorizeRequests()
                //下边的路径放行
                //配置地址放行
                .antMatchers("/demo/anyone").permitAll()
                .antMatchers("/user/login").permitAll()
                .anyRequest()
                .authenticated();    //其他地址需要认证授权
        // 将自定义的过滤器添加到Spring Security 过滤器链中
        // http.addFilterAfter(new TokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(new TokenFilter(tokenStore, restTemplate, jwtAccessTokenConverter), BasicAuthenticationFilter.class);
    }
}
