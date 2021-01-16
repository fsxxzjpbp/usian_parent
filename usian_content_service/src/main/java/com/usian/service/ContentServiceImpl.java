package com.usian.service;

import com.usian.mapper.TbContentMapper;
import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentExample;
import com.usian.redis.RedisClient;
import com.usian.utils.AdNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Resource
    private TbContentMapper tbContentMapper;

    @Value("${AD_CATEGORY_ID}")
    private Long AD_CATEGORY_ID;

    @Value("${AD_HEIGHT}")
    private Integer AD_HEIGHT;

    @Value("${AD_WIDTH}")
    private Integer AD_WIDTH;

    @Value("${AD_HEIGHTB}")
    private Integer AD_HEIGHTB;

    @Value("${AD_WIDTHB}")
    private Integer AD_WIDTHB;

    @Value("${PORTAL_AD_KEY}")
    private String PORTAL_AD_KEY;

    @Autowired
    private RedisClient redisClient;


    @Override
    public List<AdNode> selectFrontendContentByAD() {
        // 不能改不按原代码逻辑
        // 先从缓存中查询，查询到直接return
        List<AdNode> adNodeList = (List<AdNode>) redisClient.hget(PORTAL_AD_KEY, AD_CATEGORY_ID.toString());
        if (adNodeList != null && adNodeList.size() > 0) {
            return adNodeList;
        }
        // 从数据库中查询
        TbContentExample tbContentExample = new TbContentExample();
        tbContentExample.createCriteria().andCategoryIdEqualTo(AD_CATEGORY_ID);
        List<TbContent> tbContentList = tbContentMapper.selectByExample(tbContentExample);
        adNodeList = new ArrayList<>();
        for (TbContent tbContent : tbContentList) {
            AdNode adNode = new AdNode();
            adNode.setSrc(tbContent.getPic());
            adNode.setSrcB(tbContent.getPic2());
            adNode.setHref(tbContent.getUrl());
            adNode.setHeight(AD_HEIGHT);
            adNode.setWidth(AD_WIDTH);
            adNode.setHeightB(AD_HEIGHTB);
            adNode.setWidthB(AD_WIDTHB);
            adNodeList.add(adNode);
        }
        // 存入缓存中
        redisClient.hset(PORTAL_AD_KEY, AD_CATEGORY_ID.toString(), adNodeList);
        return adNodeList;
    }

    @Override
    public Integer insertTbContent(TbContent tbContent) {
        tbContent.setCreated(new Date());
        tbContent.setUpdated(new Date());
        Integer insertTbContent = tbContentMapper.insertSelective(tbContent);
        //缓存同步
        redisClient.hdel(PORTAL_AD_KEY, AD_CATEGORY_ID.toString());
        return insertTbContent;
    }

    @Override
    public Integer deleteContentByIds(Long ids) {
        Integer deleteByPrimaryKey = tbContentMapper.deleteByPrimaryKey(ids);
        //缓存同步
        redisClient.hdel(PORTAL_AD_KEY, AD_CATEGORY_ID.toString());
        return deleteByPrimaryKey;
    }
}
