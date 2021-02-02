package com.tcmyxc.service;

import com.tcmyxc.dao.UserDao;
import com.tcmyxc.domain.User;
import com.tcmyxc.result.CodeMsg;
import com.tcmyxc.vo.LoginVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

/**
 * @author 徐文祥
 * @date 2021/1/13 2:00
 */

@Service
public class UserService {

    @Autowired
    public UserDao userDao;

    public User getUserById(int id){
        return userDao.getUserById(id);
    }

    @Transactional
    public boolean tx(){
        userDao.insert(new User(2, "hhh"));
        userDao.insert(new User(1, "dfg"));

        return true;
    }
}
