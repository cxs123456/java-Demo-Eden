package org.cxs.auth.service.impl;

import org.cxs.auth.service.AuthService;
import org.cxs.auth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/*****
 * @Author: shenkunlin
 * @Date: 2019/7/7 16:23
 * @Description: com.changgou.oauth.service.impl
 ****/
@Service
public class AuthServiceImpl implements AuthService {

   /* @Autowired
    private LoadBalancerClient loadBalancerClient;*/

    @Autowired
    private RestTemplate restTemplate;


    /***
     * 授权认证方法
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     */
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        //从后往前写, 看自己需要什么
//        String host = loadBalancerClient.choose(clientId).getHost();
//        int port = loadBalancerClient.choose(clientId).getPort();
//        String url = host + port + "/oauth/token";
        // 1. url(用loadBalancerClient会报空???问老师!!!)
        String url = "http://localhost:9001/oauth/token";
        // 2. HttpMethod是一个枚举, 返回你用的请求方式
        // 3. requestEntity
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        // 加入啥, 去postman找
        body.set("grant_type", "password");
        body.set("username", username);
        body.set("password", password);
        header.set("Authorization", "Basic " + getAuthorization(clientId, clientSecret));

        ResponseEntity<Map> exchange = null;
        try {
            // 用户名或密码错误，这里会出现异常\，这里会发送2个请求
            //HttpHeaders
            exchange = restTemplate.postForEntity(url,new HttpEntity<>(body, header), Map.class);
            // exchange = restTemplate.exchange(URI.create(url), HttpMethod.POST, new HttpEntity<>(body, header), Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // 将exchange数据封装到AuthToken里
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(exchange.getBody().get("access_token").toString());
        authToken.setRefreshToken(exchange.getBody().get("refresh_token").toString());
        authToken.setJti(exchange.getBody().get("jti").toString());
        return authToken;
    }

    /**
     * 请求头的信息处理
     * @param clientId
     * @param clientSecret
     * @return
     */
    public String getAuthorization(String clientId, String clientSecret) {
        String result = clientId + ":" + clientSecret;
        byte[] bytes = Base64.getEncoder().encode(result.getBytes());
        return new String(bytes);
    }
}
