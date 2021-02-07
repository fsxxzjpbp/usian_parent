package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.*;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedisClient redisClient;

    @Value("${ITEM_BASE_INFO}")
    private String ITEM_BASE_INFO;

    @Value("${ITEM_DESC_INFO}")
    private String ITEM_DESC_INFO;

    @Value("${ITEM_PARAM_INFO}")
    private String ITEM_PARAM_INFO;

    @Value("${ITEM_INFO_EXPIRE}")
    private Long ITEM_INFO_EXPIRE;

    @Value("${SETNX_BASC_LOCK_KEY}")
    private String SETNX_BASC_LOCK_KEY;

    @Value("${SETNX_DESC_LOCK_KEY}")
    private String SETNX_DESC_LOCK_KEY;

    @Value("${SETNX_PARAM_LOCK_KEY}")
    private String SETNX_PARAM_LOCK_KEY;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Override
    public TbItem selectItemInfo(Long itemId) {
        TbItem tbItem = (TbItem) redisClient.hget(ITEM_BASE_INFO, itemId.toString());
        if (tbItem != null) {
            return tbItem;
        }
        // 防止缓存击穿：加分布式锁 设置失效时间，防止业务处理失败 造成死锁
        if (redisClient.setnx(SETNX_BASC_LOCK_KEY + ":" + itemId, itemId, 30)) {
            tbItem = tbItemMapper.selectByPrimaryKey(itemId);
            // 防止缓存穿透:如果itemId为null,也存进redis中，失效时间设置短点
            if (tbItem == null) {
                redisClient.hset(ITEM_BASE_INFO, itemId.toString(), new TbItem());
                redisClient.expire(ITEM_BASE_INFO, 30);
            }
            redisClient.hset(ITEM_BASE_INFO, itemId.toString(), tbItem);
            redisClient.expire(ITEM_BASE_INFO, ITEM_INFO_EXPIRE);
            // 释放锁
            redisClient.del(SETNX_BASC_LOCK_KEY + ":" + itemId);
            return tbItem;
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectItemInfo(itemId);
        }
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
        // 发送消息到searchService
        amqpTemplate.convertAndSend("item_exchange", "item.add", itemId);
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

    @Override
    public TbItemDesc selectItemDescByItemId(Long itemId) {
        TbItemDesc tbItemDesc = (TbItemDesc) redisClient.hget(ITEM_DESC_INFO, itemId.toString());
        if (tbItemDesc != null) {
            return tbItemDesc;
        }
        // 防止缓存击穿
        if (redisClient.setnx(SETNX_DESC_LOCK_KEY + ":" + itemId, itemId, 30)) {
            tbItemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
            // 防止缓存穿透
            if (tbItemDesc == null) {
                redisClient.hset(ITEM_DESC_INFO, itemId.toString(), new TbItemDesc());
                redisClient.expire(ITEM_DESC_INFO, 30);
            }
            redisClient.hset(ITEM_DESC_INFO, itemId.toString(), tbItemDesc);
            redisClient.expire(ITEM_DESC_INFO, ITEM_INFO_EXPIRE);
            redisClient.del(SETNX_DESC_LOCK_KEY + ":" + itemId);
            return tbItemDesc;
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectItemDescByItemId(itemId);
        }
    }

    @Override
    public TbItemParamItem selectTbItemParamItemByItemId(Long itemId) {
        TbItemParamItem tbItemParamItem = (TbItemParamItem) redisClient.hget(ITEM_PARAM_INFO, itemId.toString());
        if (tbItemParamItem != null) {
            return tbItemParamItem;
        }
        if (redisClient.setnx(SETNX_PARAM_LOCK_KEY + ":" + itemId, itemId, 30)) {
            TbItemParamItemExample example = new TbItemParamItemExample();
            example.createCriteria().andItemIdEqualTo(itemId);
            List<TbItemParamItem> itemParamItemList = tbItemParamItemMapper.selectByExampleWithBLOBs(example);
            if (itemParamItemList == null || itemParamItemList.size() == 0) {
                redisClient.hset(ITEM_PARAM_INFO, itemId.toString(), new TbItemParamItem());
                redisClient.expire(ITEM_PARAM_INFO, 30);
            }
            tbItemParamItem = itemParamItemList.get(0);
            redisClient.hset(ITEM_PARAM_INFO, itemId.toString(), tbItemParamItem);
            redisClient.expire(ITEM_PARAM_INFO, ITEM_INFO_EXPIRE);
            redisClient.del(SETNX_PARAM_LOCK_KEY + ":" + itemId);
            return tbItemParamItem;
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectTbItemParamItemByItemId(itemId);
        }
    }

    @Override
    public Integer updateTbItemByOrderId(String orderId) {
        TbOrderItemExample example = new TbOrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.selectByExample(example);
        Integer result = 0;
        for (TbOrderItem tbOrderItem : tbOrderItemList) {
            // 通过商品ID获取商品
            TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(tbOrderItem.getItemId()));
            tbItem.setNum(tbItem.getNum() - tbOrderItem.getNum());
            tbItemMapper.updateByPrimaryKeySelective(tbItem);
            result++;
        }
        return result;
    }
}
