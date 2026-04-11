package com.todaynote.controller;

import com.todaynote.common.Result;
import com.todaynote.dto.LoginRequest;
import com.todaynote.dto.RegisterRequest;
import com.todaynote.service.UserService;
import com.todaynote.support.AuthHelper;
import com.todaynote.vo.AuthUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
/**
 * 认证相关接口控制器。
 */
public class AuthController {
    
    private final UserService userService;
    private final AuthHelper authHelper;

    /**
     * 处理用户注册请求。
     *
     * @param request 注册请求体
     * @return 空成功响应
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }

    /**
     * 处理用户登录请求并返回 JWT。
     *
     * @param request 登录请求体
     * @return 登录令牌
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Valid LoginRequest request) {
        String token = userService.login(request);
        return Result.success(token);
    }

    /**
     * 获取当前登录用户信息。
     *
     * @param request HTTP 请求
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public Result<AuthUserVO> me(HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        return Result.success(userService.getCurrentUser(userId));
    }
}
