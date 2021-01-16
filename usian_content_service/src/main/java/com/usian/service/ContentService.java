package com.usian.service;

import com.usian.pojo.TbContent;
import com.usian.utils.AdNode;

import java.util.List;

public interface ContentService {

    List<AdNode> selectFrontendContentByAD();

    Integer insertTbContent(TbContent tbContent);

    Integer deleteContentByIds(Long ids);
}
