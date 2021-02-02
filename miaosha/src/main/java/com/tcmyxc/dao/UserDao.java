package com.tcmyxc.dao;

import com.tcmyxc.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 徐文祥
 * @date 2021/1/13 1:57
 */

@Mapper
public interface UserDao {

    @Select("select * from user where id = #{id}")
    public User getUserById(@Param("id") int id);

    @Insert("insert into user(id, name) values (#{id}, #{name})")
    public int insert(User user);
}
