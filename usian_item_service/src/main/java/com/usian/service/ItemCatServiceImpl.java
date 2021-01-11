package com.usian.service;


import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Resource
    private TbItemCatMapper tbItemCatMapper;


    @Override
    public List<TbItemCat> selectItemCategoryByParentId(Long id) {
        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        tbItemCatExample.
                createCriteria()
                .andParentIdEqualTo(id)
                .andStatusEqualTo(1);
        return tbItemCatMapper.selectByExample(tbItemCatExample);
        /*QueryWrapper<TbItemCat> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", id)
                .eq("status", 1);
        return tbItemCatMapper.selectList(wrapper);*/

    }

}
