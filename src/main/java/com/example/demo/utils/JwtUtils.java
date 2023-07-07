package com.example.demo.utils;

import com.example.demo.pojo.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtils {

    public static final String SUBJECT = "test";
    // 密钥
    public static String SECRET = "test";
    // 过期时间设置为60分钟
    public static final long EXPIRE = 1000 * 60 * 60;

    public String getToken(UserInfo user) {

        if (user == null) {
            return null;
        }

        return Jwts.builder().setSubject(SUBJECT)
                .claim("id", user.getId())
                .claim("account", user.getAccount())
                .claim("role", user.getRoleId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                // 设置签名方式以及密钥 这里密钥采用用户密码
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    // 由返回的结果可以拿到token中的数据
    public static Claims checkToken(String token) {
        return Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    }

//    public boolean verifyToken(String token) {
//        Claims claims = checkToken(token);
//        String account = (String) claims.get("account");
//        String redisToken = (String) redisUtils.get(account);
//        return redisToken.equals(token);
//    }

}
