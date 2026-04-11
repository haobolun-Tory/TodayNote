package com.todaynote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.todaynote.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
