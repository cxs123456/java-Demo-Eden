package org.cxs.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * note:
 *
 * @author Administrator
 * @date 2021/11/25 17:20
 **/
@Configuration
@EnableWebSecurity
@Order(-1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Bean
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//        manager.createUser(User.withDefaultPasswordEncoder().username("123").password("123456").roles("USER").build());
//        return manager;
//    }

    /**
     * 身份认证配置
     * 配置 认证用户
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("aaa").password(passwordEncoder().encode("123")).roles("USER")
                .and().withUser("cc").password("123").roles("USER");

        /*1.基于内存的身份认证*/
//        auth.inMemoryAuthentication().passwordEncoder(encoder)
//                .withUser("admin").password(encoder.encode("admin")).roles("admin")
//                .and()
//                .withUser("cai").password(encoder.encode("123457")).roles("common");


        /*2.使用UserDetails进行身份认证*/
        // auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }


    /**
     * 请求授权配置
     * 配置 请求拦截权限
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // super.configure(http);
        /*自定义访问控制*/
        http.authorizeRequests()
                // 根据用户拥有的不同角色、不同权限配置访问权限
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").access("hasAnyRole('ADMIN','USER')")
                .antMatchers("/db/**").access("hasAnyRole('ADMIN') and  hasRole('DBA')")
                // 表示除了前面定义的url,后面的都得认证后访问（登陆后访问）
                .anyRequest().authenticated()
                .antMatchers("/demo").permitAll()  // 改请求url 表示放行访问

                // 表示开启表单登陆，就是一开始看到的登陆界面，登陆url为/login,permitAll表示和登陆相关的接口不需要认证
                .and()
                .formLogin()// 开启表单认证
                .loginProcessingUrl("/url")
                .permitAll() // 登录请求 放行
                .loginPage("/login_page")                      //登陆页面
                .loginProcessingUrl("/login")           //登陆请求处理接口
                // .successHandler(AuthenticationSuccessHandler) //登陆成功后
                // .failureHandler(AuthenticationFailureHandler) //登陆失败后

                .and()
                .logout() //开启注销登陆
                .logoutUrl("/logout") //注销登陆请求url
                .clearAuthentication(true)  //清除身份信息
                .invalidateHttpSession(true)    //session失效
                // .addLogoutHandler // 登出处理器
                // .logoutSuccessHandler //注销成功处理

                .and().csrf().disable();// 表示关闭csrf（Cross-site request forgery）

        // configure(HttpSecurity)允许基于选择匹配在资源级配置基于网络的安全性。以下示例将以/ admin /开头的网址限制为具有ADMIN角色的用户，
        // 并声明任何其他网址需要成功验证。
        // 也就是对角色的权限——所能访问的路径做出限制
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated();
    }

    /**
     * web安全配置 <bt/>
     * configure(WebSecurity)用于影响全局安全性(配置资源，设置调试模式，通过实现自定义防火墙定义拒绝请求)的配置设置。
     * 一般用于配置全局的某些通用事物，例如静态资源等
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {

        //super.configure(web);
        web.ignoring().antMatchers("/css/**", "/js/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
