package org.cxs.test;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DemoTest {

    // 创建token
    @Test
    public void createToken(){
        // 创建JWT
        JwtBuilder builder = Jwts.builder();
        // 构建头信息
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("keyId", "JWT");
        builder.setHeader(map);
        // 构建载荷信息
        builder.setId("001");
        builder.setIssuer("张三");
        // builder.setIssuedAt(new Date());
        // 添加签名
        builder.signWith(SignatureAlgorithm.HS256, "itheima");
        // builder.setExpiration(new Date(System.currentTimeMillis() + 30000));    // 30秒后过期
        // 生成token
        String token = builder.compact();
        System.out.println("token:" + token);
    }

    @Test
    public void testParseToken(){
        // 被解析的令牌
        String token = "eyJrZXlJZCI6IkpXVCIsImFsZyI6IkhTMjU2In0.eyJqdGkiOiIwMDEiLCJpc3MiOiLlvKDkuIkiLCJpYXQiOjE2MDU5NjE0ODh9.gimbUgI99kJ8IEYKcJqO59FhPnRfqrYJiB1v8saX5as";
        // 创建解析对象
        JwtParser parser = Jwts.parser();
        parser.setSigningKey("itheima");
        Claims claims = parser.parseClaimsJws(token).getBody();
        System.out.println(claims);
    }
}
