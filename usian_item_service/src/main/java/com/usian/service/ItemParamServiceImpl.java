package com.usian.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamExample;
import com.usian.utils.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ItemParamServiceImpl implements ItemParamService {

    @Resource
    private TbItemParamMapper tbItemParamMapper;


    @Override
    public TbItemParam selectItemParamByItemCatId(Long itemCatId) {
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        tbItemParamExample.createCriteria().andItemCatIdEqualTo(itemCatId);
        /*QueryWrapper<TbItemParam> wrapper = new QueryWrapper<>();
        wrapper.eq("item_cat_id", itemCatId);*/
        List<TbItemParam> tbItemParams = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);
        if (tbItemParams != null && tbItemParams.size() > 0) {
            return tbItemParams.get(0);
        }
        return null;
    }

    @Override
    public PageResult selectItemParamAll(Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        tbItemParamExample.setOrderByClause("updated desc");
        List<TbItemParam> tbItemParamList = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);
        PageInfo<TbItemParam> tbItemParamPageInfo = new PageInfo<>(tbItemParamList);
        return new PageResult(tbItemParamPageInfo.getPageNum(), tbItemParamPageInfo.getTotal(), tbItemParamPageInfo.getList());
    }

    @Override
    public Integer insertItemParam(Long itemCatId, String paramData) {
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        tbItemParamExample.createCriteria().andItemCatIdEqualTo(itemCatId);
        /*QueryWrapper<TbItemParam> wrapper = new QueryWrapper<>();
        wrapper.eq("item_cat_id", itemCatId);*/
        List<TbItemParam> tbItemParamList = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);
        if (tbItemParamList != null && tbItemParamList.size() > 0) {
            return 0;
        }
        Date date = new Date();
        TbItemParam tbItemParam = new TbItemParam();
        tbItemParam.setParamData(paramData);
        tbItemParam.setItemCatId(itemCatId);
        tbItemParam.setUpdated(date);
        tbItemParam.setCreated(date);
        return tbItemParamMapper.insertSelective(tbItemParam);
    }

    @Override
    public Integer deleteItemParamById(Long id) {
        return tbItemParamMapper.deleteByPrimaryKey(id);
    }

}
