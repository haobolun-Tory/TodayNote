package com.todaynote.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;

import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    // 实际生产中应从配置文件读取
    private static final byte[] KEY = "TodayNoteSecretKey123".getBytes();
    private static final JWTSigner SIGNER = JWTSignerUtil.hs256(KEY);

    /**
     * 创建 JWT 令牌。
     */
    public static String createToken(Long userId, String username) {
        Map<String, Object> payload = new HashMap<>();
        // 写入标准时间字段，便于后续令牌校验与过期处理。
        payload.put(JWTPayload.ISSUED_AT, System.currentTimeMillis());
        payload.put(JWTPayload.EXPIRES_AT, System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7); // 7天过期
        payload.put("uid", userId);
        payload.put("username", username);
        
        return JWT.create()
                .addPayloads(payload)
                .setSigner(SIGNER)
                .sign();
    }

    /**
     * 校验 JWT 是否可用。
     */
    public static boolean verify(String token) {
        try {
            return JWT.of(token).setSigner(SIGNER).verify();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从 JWT 中提取用户 ID。
     */
    public static Long getUserId(String token) {
        try {
            final JWT jwt = JWT.of(token);
            return Long.valueOf(jwt.getPayload("uid").toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从 JWT 中提取用户名。
     */
    public static String getUsername(String token) {
        try {
            final JWT jwt = JWT.of(token);
            Object username = jwt.getPayload("username");
            return username == null ? null : username.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
