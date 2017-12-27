package com.knms.shop.android.bean.body.orderpay;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 654654 on 2017/8/22.
 */

public class OrderTradingBos {
    @SerializedName("tradingId")
    public String tradingId;//订单ID, 为null 或不存在 标示订单不纯在，如果存在 表示当前订单存在，此ID 需要用在订单 创建 接口上
    @SerializedName("shopIsHide")
    public String shopIsHide;//是否隐藏商家信息 1 正常 2 隐藏 ：不会有 shopId 传入，APP 不提供跳转功能
    @SerializedName("shopId")
    public String shopId;//	商家ID
    @SerializedName("shopName")
    public String shopName;//商家名
    @SerializedName("actualMoney")
    public double actualMoney;//订单 实际付款金额 展示字符
    @SerializedName("effectiveNumber")
    public int effectiveNumber;//订单商品数量
    @SerializedName("tradingTransportMoneyTitle")
    public String tradingTransportMoneyTitle;//实际运费 展示金额 或标题，（1000、待协商、免运费） 展示字符
    @SerializedName("tradingSummaryTitle")
    public String tradingSummaryTitle;//	订单小结 订单小结算，交易小 ( 共 X件商品，合计：￥XXX （含配送费￥XX）)
    @SerializedName("tradingStatus")
    public String tradingStatus;//交易状态 标记码 100、200、300等
    @SerializedName("tradingStatusTitle")
    public String tradingStatusTitle;//交易状态 比如 ，待送货、交易关闭，等
    @SerializedName("tradingLocking")
    public String tradingLocking;//订单锁定(1锁定 0未锁定)
    @SerializedName("userAppraise")
    public String userAppraise;//用户是否评价 1为已评价
    @SerializedName("businessmenAppraise")
    public String businessmenAppraise;//用户是否已经回复评价 1已回复
    @SerializedName("isComplaint")
    public int isComplaint;//1表示没有投诉，2表示投诉中
    @SerializedName("complaintCount")
    public int complaintCount;//等于零表示没有投诉,大于零表示有投诉
    @SerializedName("orderTradingCommoditys")
    public List<OrderTradingCommoditys> orderTradingCommoditys;//订单下的商品信息
}
