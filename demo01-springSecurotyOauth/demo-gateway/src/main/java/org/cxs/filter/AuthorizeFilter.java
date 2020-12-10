package org.cxs.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class AuthorizeFilter implements GlobalFilter, Ordered {

    private static final String AUTHORIZE_TOKEN = "Authorization";

    /**
     * 过滤
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 获取请求url
        String path = request.getURI().getPath();
        // 判断是否是登录的地址
        if (path.startsWith("/auth/user/login")) {
            // 可以直接不拦截 认证服务的 /auth/token 相关接口，改接口是oauth2的token接口
            //if (StringUtils.startsWithAny(path, "/auth/user/login","/auth/auth/token")){
            //是就放行
            return chain.filter(exchange);
        }
        // 从url中获取token
        // String token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);

        // 从header中获取token
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
       /* if (token == null) {
            token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
        }*/

        // 从cookie中获取到token
        if (token == null) {
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (cookie == null) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
            token = cookie.getValue();
            // 将cookie存入请求头，构造 bearer授权访问
            token = "bearer " + token;
            request.mutate().header(AUTHORIZE_TOKEN, token);
        }

        // token为空, 返回401
        if (token == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // z.减轻网关服务压力，不解析token了，交给资源服务解析（所有资源服务都有public.key）
//        // 4、token存在，需要解析token
//        try {
//            // 解析成功
//            Claims claims = JwtUtil.parseJWT(token);
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 解析失败
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);    // 设置响应状态码
//            return response.setComplete();
//        }


        // z.判断令牌是否过期，以及调用刷新令牌重新生成令牌放到 response里面

        //放行
        return chain.filter(exchange);

    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        // 数字越小, 顺序越大
        return 0;
    }
}
