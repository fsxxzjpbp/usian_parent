package com.usian.controller;


import com.usian.feign.SearchItemFeign;
import com.usian.pojo.SearchItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/frontend/searchItem")
public class SearchItemController {

    @Autowired
    private SearchItemFeign searchItemFeign;

    @RequestMapping("/importAll")
    public Result importAll() {
        Boolean importAll = searchItemFeign.importAll();
        if (importAll) {
            return Result.ok();
        }
        return Result.error("导入失败！");
    }

    @RequestMapping("/list")
    public List<SearchItem> list(String q, @RequestParam(defaultValue = "1") Integer pages, @RequestParam(defaultValue = "20") Integer rows) {
        return searchItemFeign.list(q, pages, rows);
    }
}
