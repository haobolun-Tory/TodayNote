package com.todaynote.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Length(min = 4, max = 20, message = "用户名长度必须在4-20之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度必须在6-20之间")
    private String password;
    
    private String nickname;
    private String email;
}
