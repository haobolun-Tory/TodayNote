package com.todaynote.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.todaynote.dto.LoginRequest;
import com.todaynote.dto.RegisterRequest;
import com.todaynote.entity.User;
import com.todaynote.mapper.UserMapper;
import com.todaynote.service.UserService;
import com.todaynote.utils.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public void register(RegisterRequest request) {
        // 1. 检查用户名是否存在
        long count = this.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 创建用户对象
        User user = new User();
        user.setUsername(request.getUsername());
        user.setNickname(StringUtils.hasText(request.getNickname()) ? request.getNickname() : request.getUsername());
        user.setEmail(request.getEmail());
        
        // 3. 密码加密存储
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        // 4. 保存
        this.save(user);
    }

    @Override
    public String login(LoginRequest request) {
        // 1. 根据用户名查询
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername()));
        
        if (user == null) {
            throw new RuntimeException("用户不存在或密码错误");
        }

        // 2. 校验密码
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户不存在或密码错误");
        }
        
        // 3. 生成JWT
        return JwtUtils.createToken(user.getId(), user.getUsername());
    }
}
