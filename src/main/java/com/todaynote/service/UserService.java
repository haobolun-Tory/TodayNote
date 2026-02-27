package com.todaynote.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.todaynote.dto.LoginRequest;
import com.todaynote.dto.RegisterRequest;
import com.todaynote.entity.User;

public interface UserService extends IService<User> {
    /**
     * 注册用户
     * @param request 注册请求参数
     */
    void register(RegisterRequest request);

    /**
     * 用户登录
     * @param request 登录请求参数
     * @return JWT Token
     */
    String login(LoginRequest request);
}
