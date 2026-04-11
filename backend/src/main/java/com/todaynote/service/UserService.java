package com.todaynote.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.todaynote.dto.LoginRequest;
import com.todaynote.dto.RegisterRequest;
import com.todaynote.entity.User;
import com.todaynote.vo.AuthUserVO;

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

    /**
     * 获取当前登录用户信息。
     *
     * @param userId 用户 ID
     * @return 当前用户视图对象
     */
    AuthUserVO getCurrentUser(Long userId);
}
