package org.cxs.auth.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.cxs.auth.util.AuthToken;
import org.cxs.auth.util.CookieTools;
import org.cxs.auth.util.TokenDecode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * @author cxs
 **/
@Slf4j
public class TokenFilter extends GenericFilterBean {

    private final TokenStore tokenStore;

    private final JwtAccessTokenConverter jwtAccessTokenConverter;

    private final RestTemplate restTemplate;


    public TokenFilter(TokenStore tokenStore, RestTemplate restTemplate, JwtAccessTokenConverter jwtAccessTokenConverter) {
        this.tokenStore = tokenStore;
        this.restTemplate = restTemplate;
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(token) &&
                SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2Authentication) {
            OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
            String tokenValue = details.getTokenValue();
            // 获取token是否过期
            Map<String, String> userInfo = TokenDecode.getUserInfo();
            DefaultOAuth2AccessToken accessToken = (DefaultOAuth2AccessToken) tokenStore.readAccessToken(tokenValue);
            // jwt的过期机制是 payload中 exp 的值是时间戳，exp比较服务器当前时间的大小
            if (!accessToken.isExpired()) {
                // 如果剩余10分钟 就，调用刷新token ,重新生成
                int expiresIn = accessToken.getExpiresIn();
                if ((expiresIn / 60) < 1) {
                    // 方式1：使用 刷新token获取 新的token 和 刷新token，放入cookie中
                    // 获取刷新token
                    String refreshToken = CookieTools.getCookieValue(httpServletRequest, "refresh_token");
                    if (StringUtils.isNotBlank(refreshToken)) {
                        // 调用刷新token的接口，获取新的token和刷新token
                        AuthToken authToken = refreshToken(refreshToken);
                        if (authToken != null) {
                            String newTokenVal = authToken.getAccessToken();
                            String newRefreshToken = authToken.getRefreshToken();
                            CookieTools.setCookie(httpServletRequest, httpServletResponse, "Authorization-new", newTokenVal);
                            CookieTools.setCookie(httpServletRequest, httpServletResponse, "refresh_token", newRefreshToken);
                        }
                    }

                    // 方式2：取巧，将 token 的payload中exp属性的时间修改更长，再加密生成新的token放入cookie中
                    accessToken.setExpiration(DateUtils.addSeconds(new Date(), 120));
                    OAuth2AccessToken enhance = jwtAccessTokenConverter.enhance(accessToken, authentication);
                    String newTokenVal = enhance.getValue();
                    log.warn(newTokenVal);
                    CookieTools.setCookie(httpServletRequest, httpServletResponse, "Authorization-new", newTokenVal);
                }
            }
        }
        filterChain.doFilter(request, response);
    }


    public AuthToken refreshToken(String refreshToken) {
        String url = "http://localhost:9001/oauth/token";
        // 2. HttpMethod是一个枚举, 返回你用的请求方式
        // 3. requestEntity
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        // 加入啥, 去postman找
        body.set("grant_type", "refresh_token");
        body.set("refresh_token", refreshToken);
        // body.set("password", password);
        header.set("Authorization", "Basic " + getAuthorization("web", "123456"));

        ResponseEntity<Map> exchange = null;
        try {
            // 用户名或密码错误，这里会出现异常\，这里会发送2个请求
            //HttpHeaders
            exchange = restTemplate.postForEntity(url, new HttpEntity<>(body, header), Map.class);
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
     *
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
