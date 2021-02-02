package com.tcmyxc.dao;

import com.tcmyxc.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @author 徐文祥
 * @date 2021/1/13 23:10
 */

@Mapper
public interface MiaoshaUserDao {

    @Select("select * from miaosha_user where id = #{id}")
    public MiaoshaUser getById(@Param("id")long id);

    // 不更新的字段不要往更新语句里面传递
    @Update("update miaosha_user set password = #{password} where id = #{id}")
    void update(MiaoshaUser toBeUpdateUser);
}
