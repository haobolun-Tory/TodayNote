package com.todaynote.support;

import com.todaynote.common.BizException;
import com.todaynote.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
/**
 * 认证辅助类，用于从请求中提取和校验当前用户身份。
 */
public class AuthHelper {

    /**
     * 尝试获取当前登录用户 ID，失败时返回空。
     */
    public Long getCurrentUserId(HttpServletRequest request) {
        String token = extractToken(request);
        if (!StringUtils.hasText(token) || !JwtUtils.verify(token)) {
            return null;
        }
        return JwtUtils.getUserId(token);
    }

    /**
     * 强制获取当前登录用户 ID，不存在时抛出未登录异常。
     */
    public Long requireCurrentUserId(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            throw new BizException(40100, "请先登录");
        }
        return userId;
    }

    /**
     * 从 Authorization 请求头中提取 Bearer Token。
     */
    public String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
