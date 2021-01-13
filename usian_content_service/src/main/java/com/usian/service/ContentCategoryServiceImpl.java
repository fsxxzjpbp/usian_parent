package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbContentCategoryMapper;
import com.usian.mapper.TbContentMapper;
import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentCategory;
import com.usian.pojo.TbContentCategoryExample;
import com.usian.pojo.TbContentExample;
import com.usian.utils.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Resource
    private TbContentCategoryMapper tbContentCategoryMapper;

    @Resource
    private TbContentMapper tbContentMapper;

    @Override
    public List<TbContentCategory> selectContentCategoryByParentId(Long id) {
        TbContentCategoryExample example = new TbContentCategoryExample();
        example.createCriteria().andParentIdEqualTo(id);
        return tbContentCategoryMapper.selectByExample(example);
    }

    @Override
    public Integer insertContentCategory(TbContentCategory tbContentCategory) {
        // 补全商品分类信息
        tbContentCategory.setStatus(1);
        tbContentCategory.setSortOrder(1);
        tbContentCategory.setIsParent(false);
        tbContentCategory.setCreated(new Date());
        tbContentCategory.setUpdated(new Date());
        Integer insertNum = tbContentCategoryMapper.insertSelective(tbContentCategory);

        TbContentCategory contentCategory = tbContentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());
        if (!contentCategory.getIsParent()) {
            contentCategory.setIsParent(true);
            contentCategory.setUpdated(new Date());
            tbContentCategoryMapper.updateByPrimaryKey(contentCategory);
        }
        return insertNum;
    }

    @Override
    public Integer deleteContentCategoryById(Long categoryId) {
        TbContentCategory contentCategory = tbContentCategoryMapper.selectByPrimaryKey(categoryId);
        // 查询是否为父节点
        if (contentCategory.getIsParent()) {
            return 0;
        }
        // 不为父节点时
        Integer updateByPrimaryKeySelective = tbContentCategoryMapper.deleteByPrimaryKey(contentCategory.getId());
        // 查询是否有兄弟节点 有则变父节点不为父节点
        TbContentCategoryExample tbContentCategoryExample = new TbContentCategoryExample();
        tbContentCategoryExample.createCriteria().andParentIdEqualTo(contentCategory.getParentId());
        List<TbContentCategory> tbContentCategoryList = tbContentCategoryMapper.selectByExample(tbContentCategoryExample);
        if (tbContentCategoryList.size() == 0) {
            TbContentCategory contentCategory1 = new TbContentCategory();
            contentCategory1.setId(contentCategory.getParentId());
            contentCategory1.setIsParent(false);
            contentCategory1.setUpdated(new Date());
            tbContentCategoryMapper.updateByPrimaryKeySelective(contentCategory1);
        }
        return updateByPrimaryKeySelective;
    }

    @Override
    public Integer updateContentCategory(TbContentCategory tbContentCategory) {
        // 补全信息
        tbContentCategory.setUpdated(new Date());
        return tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
    }

    @Override
    public PageResult selectTbContentAllByCategoryId(Long categoryId, Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        TbContentExample tbContentExample = new TbContentExample();
        tbContentExample.setOrderByClause("updated desc");
        tbContentExample.createCriteria().andCategoryIdEqualTo(categoryId);
        List<TbContent> tbContentList = tbContentMapper.selectByExampleWithBLOBs(tbContentExample);
        PageInfo<TbContent> tbContentPageInfo = new PageInfo<>(tbContentList);
        return new PageResult(tbContentPageInfo.getPageNum(), tbContentPageInfo.getTotal(), tbContentPageInfo.getList());
    }

    @Override
    public Integer insertTbContent(TbContent tbContent) {
        tbContent.setCreated(new Date());
        tbContent.setUpdated(new Date());
        return tbContentMapper.insertSelective(tbContent);
    }

    @Override
    public Integer deleteContentByIds(Long ids) {
        return tbContentMapper.deleteByPrimaryKey(ids);
    }
}
