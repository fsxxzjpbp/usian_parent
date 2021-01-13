package com.usian.controller;

import com.usian.feign.ContentServiceFeign;
import com.usian.utils.AdNode;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/frontend/content")
public class ContentController {
    @Autowired
    private ContentServiceFeign contentServiceFeign;

    @RequestMapping("/selectFrontendContentByAD")
    public Result selectFrontendContentByAD() {
        List<AdNode> tbContentList = contentServiceFeign.selectFrontendContentByAD();
        if (tbContentList != null && tbContentList.size() > 0) {
            return Result.ok(tbContentList);
        }
        return Result.error("查询广告失败！");
    }
}
