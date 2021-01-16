package com.usian.controller;

import com.usian.pojo.TbContent;
import com.usian.service.ContentService;
import com.usian.utils.AdNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/content")
public class ContentController {
    @Autowired
    private ContentService contentService;

    @RequestMapping("/selectFrontendContentByAD")
    List<AdNode> selectFrontendContentByAD() {
        return contentService.selectFrontendContentByAD();
    }

    @RequestMapping("/insertTbContent")
    Integer insertTbContent(@RequestBody TbContent tbContent) {
        return contentService.insertTbContent(tbContent);
    }

    @RequestMapping("/deleteContentByIds")
    Integer deleteContentByIds(Long ids) {
        return contentService.deleteContentByIds(ids);
    }
}
