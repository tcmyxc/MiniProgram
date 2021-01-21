package com.tcmyxc.controller;

import com.tcmyxc.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author tcmyxc
 * @date 2021/1/6 20:38
 */
@RestController
public class ContentController {

    @Autowired
    private ContentService contentService;

    @GetMapping("/search/{keyword}")
    public List<Map<String, Object>> parse(@PathVariable("keyword") String keyword) throws IOException {
        return contentService.searchPage(keyword, 1, 12);
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> search(@PathVariable("keyword") String keyword,
                                            @PathVariable("pageNo") int pageNo,
                                            @PathVariable("pageSize") int pageSize) throws IOException {
        if(pageNo <= 1){
            pageNo = 1;
        }

        return contentService.searchPage(keyword, pageNo, pageSize);

    }

}
