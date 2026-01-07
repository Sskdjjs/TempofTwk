package com.example.demo.mapper;


// UserMapper.java

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {


    @Select("SELECT COUNT(*) FROM user WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM user WHERE email = #{email}")
    int countByEmail(@Param("email") String email);
//select by email or user
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);
}