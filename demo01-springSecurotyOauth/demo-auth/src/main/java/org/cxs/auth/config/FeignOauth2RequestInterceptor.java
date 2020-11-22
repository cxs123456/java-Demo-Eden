package org.cxs.auth.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.cxs.auth.util.JwtToken;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Configuration
public class FeignOauth2RequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        //创建令牌信息，bearer授权访问
        String token ="Bearer "+ JwtToken.adminJwt();
        //将令牌添加到头文件中
        requestTemplate.header("Authorization",token);
        try {
            //使用RequestContextHolder工具获取request相关变量
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                //取出request
                HttpServletRequest request = attributes.getRequest();
                //获取所有头文件信息的key
                Enumeration<String> headerNames = request.getHeaderNames();
                if (headerNames != null) {
                    while (headerNames.hasMoreElements()) {
                        //头文件的key
                        String name = headerNames.nextElement();
                        //头文件的value
                        String values = request.getHeader(name);
                        //将令牌数据添加到头文件中
                        requestTemplate.header(name, values);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
