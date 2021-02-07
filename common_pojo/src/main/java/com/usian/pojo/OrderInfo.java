package com.usian.pojo;

public class OrderInfo {
    private TbOrderShipping tbOrderShipping;
    private TbOrder tbOrder;
    private String orderItem;

    public TbOrderShipping getTbOrderShipping() {
        return tbOrderShipping;
    }

    public void setTbOrderShipping(TbOrderShipping tbOrderShipping) {
        this.tbOrderShipping = tbOrderShipping;
    }

    public TbOrder getTbOrder() {
        return tbOrder;
    }

    public void setTbOrder(TbOrder tbOrder) {
        this.tbOrder = tbOrder;
    }

    public String getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(String orderItem) {
        this.orderItem = orderItem;
    }
}
