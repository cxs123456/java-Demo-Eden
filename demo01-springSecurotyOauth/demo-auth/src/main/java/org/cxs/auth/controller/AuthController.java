package org.cxs.auth.controller;

import org.cxs.auth.service.AuthService;
import org.cxs.auth.util.AuthToken;
import org.cxs.auth.util.CookieTools;
import org.cxs.auth.util.Result;
import org.cxs.auth.util.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*****
 * @Author: shenkunlin
 * @Date: 2019/7/7 16:42
 * @Description: com.changgou.oauth.controller
 ****/
@RestController
@RequestMapping(value = "/user")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    @Value("${auth.cookieMaxAge}")
    private Integer cookieMaxAge;

    /**
     * 可以直接 调用 /oauth/token，不必通过login控制器
     * @param response
     * @param request
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/login")
    public Result login(HttpServletResponse response, HttpServletRequest request,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password) {
        // 要啥给啥
        AuthToken authToken = authService.login(username, password, clientId, clientSecret);
        if (authToken != null) {
//            CookieUtil.addCookie(response, // 响应数据
//                                cookieDomain, // 放到cookie的key
//                           "/", // 存的cookie的路径
//                          "Authorization", // cookie的名字
//                                authToken.getAccessToken(), // cookie的值
//                                cookieMaxAge, // cookie的有效期
//                       false); // 是否用http
            CookieTools.setCookie(request, response, "Authorization", authToken.getAccessToken());
            CookieTools.setCookie(request, response, "cuname", username);
            CookieTools.setCookie(request, response, "refresh_token", authToken.getRefreshToken());
            return new Result(true, StatusCode.OK, "登录成功", authToken);
        }else {
            return new Result(true, StatusCode.LOGINERROR, "用户名或密码错误");
        }

    }
}
