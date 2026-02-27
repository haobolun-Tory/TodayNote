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

    public static String createToken(Long userId, String username) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(JWTPayload.ISSUED_AT, System.currentTimeMillis());
        payload.put(JWTPayload.EXPIRES_AT, System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7); // 7天过期
        payload.put("uid", userId);
        payload.put("username", username);
        
        return JWT.create()
                .addPayloads(payload)
                .setSigner(SIGNER)
                .sign();
    }

    public static boolean verify(String token) {
        try {
            return JWT.of(token).setSigner(SIGNER).verify();
        } catch (Exception e) {
            return false;
        }
    }

    public static Long getUserId(String token) {
        try {
            final JWT jwt = JWT.of(token);
            return Long.valueOf(jwt.getPayload("uid").toString());
        } catch (Exception e) {
            return null;
        }
    }
}
