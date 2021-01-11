package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemCatMapper;
import com.usian.mapper.TbItemDescMapper;
import com.usian.mapper.TbItemMapper;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.pojo.*;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    @Resource
    private TbItemMapper tbItemMapper;
    @Resource
    private TbItemDescMapper tbItemDescMapper;
    @Resource
    private TbItemParamItemMapper tbItemParamItemMapper;
    @Resource
    private TbItemCatMapper tbItemCatMapper;


    @Override
    public TbItem selectItemInfo(Long itemId) {
        return tbItemMapper.selectByPrimaryKey(itemId);
    }

    /**
     * 查询商品列表信息
     *
     * @param page 当前页
     * @param rows 每页几条记录
     * @return PageResult
     */
    @Override
    public PageResult selectTbItemAllByPage(Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        TbItemExample tbItemExample = new TbItemExample();
        tbItemExample.setOrderByClause("updated desc");
        tbItemExample.createCriteria().andStatusEqualTo((byte) 1);
        List<TbItem> tbItemList = tbItemMapper.selectByExample(tbItemExample);
        // 商品价格单位为分  要除100
        for (int i = 0; i < tbItemList.size(); i++) {
            TbItem tbItem = tbItemList.get(i);
            tbItem.setPrice(tbItem.getPrice() / 100);
        }
        PageInfo<TbItem> tbItemPageInfo = new PageInfo<>(tbItemList);
        return new PageResult(tbItemPageInfo.getPageNum(), tbItemPageInfo.getTotal(), tbItemPageInfo.getList());
    }

    @Override
    public Integer insertTbItem(TbItem tbItem, String desc, String itemParams) {
        // 补齐商品的信息
        //商品ID非自增长，使用工具类获取
        long itemId = IDUtils.genItemId();
        Date date = new Date();
        tbItem.setPrice(tbItem.getPrice() * 100);
        tbItem.setId(itemId);
        tbItem.setStatus((byte) 1);
        tbItem.setCreated(date);
        tbItem.setUpdated(date);
        Integer tbItemNum = tbItemMapper.insertSelective(tbItem);
        // 补齐商品描述
        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setCreated(date);
        tbItemDesc.setUpdated(date);
        Integer tbItemDescNum = tbItemDescMapper.insertSelective(tbItemDesc);
        // 补齐商品规格参数
        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setItemId(itemId);
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setCreated(date);
        tbItemParamItem.setUpdated(date);
        Integer tbItemParamItemNum = tbItemParamItemMapper.insertSelective(tbItemParamItem);
        return tbItemNum + tbItemDescNum + tbItemParamItemNum;
    }

    @Override
    public Integer deleteItemById(Long itemId) {
        // 只需要改变状态就可以了
        //Integer deleteByPrimaryKey = itemService.deleteByPrimaryKey(itemId);
        TbItem tbItem = new TbItem();
        tbItem.setId(itemId);
        tbItem.setStatus((byte) 3);
        tbItem.setUpdated(new Date());
        return tbItemMapper.updateByPrimaryKeySelective(tbItem);
    }

    /**
     * 查询商品信息
     *
     * @param itemId 商品的ID
     * @return tbItem
     */
    @Override
    public Map<String, Object> preUpdateItem(Long itemId) {
        Map<String, Object> map = new HashMap<>();
        // 根据ID查询商品信息
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        map.put("item", tbItem);
        // 根据Id查询商品描述
        TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
        map.put("itemDesc", tbItemDesc.getItemDesc());
        // 根据Id查询商品的类目
        TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(tbItem.getCid());
        map.put("itemCat", tbItemCat.getName());
        // 根据Id查询商品规格参数
        TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        tbItemParamItemExample.createCriteria().andItemIdEqualTo(itemId);
        List<TbItemParamItem> list = tbItemParamItemMapper.selectByExampleWithBLOBs(tbItemParamItemExample);
        if (list != null && list.size() > 0) {
            map.put("itemParamItem", list.get(0).getParamData());
        }
        return map;
    }

    /**
     * 修改商品
     *
     * @param tbItem     商品信息
     * @param desc       描述
     * @param itemParams 规格
     * @return Integer
     */
    @Override
    public Integer updateTbItem(TbItem tbItem, String desc, String itemParams) {
        // 补全item信息
        // 更新时间
        Date date = new Date();
        tbItem.setPrice(tbItem.getPrice() * 100);
        tbItem.setUpdated(date);
        Integer tbItemNum = tbItemMapper.updateByPrimaryKeySelective(tbItem);
        // 根据获取ItemDesc
        // 获取商品的ID
        Long itemId = tbItem.getId();
        TbItemDesc tbItemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setUpdated(date);
        Integer tbItemDescNum = tbItemDescMapper.updateByPrimaryKeySelective(tbItemDesc);
        // 根据ID获取商品规格
        TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        tbItemParamItemExample.createCriteria().andItemIdEqualTo(itemId);
        List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(tbItemParamItemExample);
        tbItemParamItems.get(0).setParamData(itemParams);
        tbItemParamItems.get(0).setUpdated(date);
        Integer tbItemParamItemNum = tbItemParamItemMapper.updateByPrimaryKeyWithBLOBs(tbItemParamItems.get(0));
        return tbItemNum + tbItemDescNum + tbItemParamItemNum;
    }
}
