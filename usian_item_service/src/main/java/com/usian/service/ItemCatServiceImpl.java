package com.usian.service;


import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import com.usian.utils.CatNode;
import com.usian.utils.CatResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Override
    public CatResult selectItemCategoryAll() {
        CatResult catResult = new CatResult();
        catResult.setData(getCatList(0L));
        return catResult;
    }

    private List<?> getCatList(long parentId) {
        // 查询
        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        tbItemCatExample.createCriteria().andParentIdEqualTo(parentId);
        List<TbItemCat> tbItemCatList = tbItemCatMapper.selectByExample(tbItemCatExample);
        List<Object> parentList = new ArrayList();
        // 首页不能把父节点全展示玩，规定展示到多少条跳出递归
        Integer count = 0;
        for (TbItemCat tbItemCat : tbItemCatList) {
            // 判断是否为父节点
            if (tbItemCat.getIsParent()) {
                CatNode catNode = new CatNode();
                catNode.setName(tbItemCat.getName());
                catNode.setItem(getCatList(tbItemCat.getId()));
                parentList.add(catNode);
                count++;
                if (count == 18) {
                    break;
                }
            } else {
                // 不为父节点
                parentList.add(tbItemCat.getName());
            }
        }
        return parentList;
    }

}
