package com.tcmyxc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author tcmyxc
 * @date 2021/1/6 17:50
 */

@Controller
public class IndexController {

    @RequestMapping({"/", "/index"})
    public String getIndex(){
        return "index";
    }
}
