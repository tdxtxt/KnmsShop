package com.knms.shop.android.bean.body.orderpay;

import com.google.gson.annotations.SerializedName;

/**
 * app支付订单列表商品信息
 */
public class OrderTradingCommoditys {
    @SerializedName("tradingCommodityId")
    public String tradingCommodityId;//商品与订单关联系统ID
    @SerializedName("showId")
    public String showId;//展示商品 系统id
    @SerializedName("showName")
    public String showName;//展示商品 名
    @SerializedName("specificationId")
    public String specificationId;//规格商品 id
    @SerializedName("specificationImg")
    public String specificationImg;//规格商品图片
    @SerializedName("specificationStatusTitle")
    public String specificationStatusTitle;//规格 状态 标题（比如 退货）
    @SerializedName("parameterBriefing")
    public String parameterBriefing;//规格属性的简述
    @SerializedName("totalShowMoney")
    public String totalShowMoney;//展示交易价格 单价* 数量
    @SerializedName("buyNumber")
    public String buyNumber;//	商实际购买数量
    @SerializedName("totalRealityMoney")
    public String totalRealityMoney;//实际交易价格 单价* 数量
    public String showMoney;//展示交易价格 单价
    public String realityMoney;//实际交易价格 单价
    public String tradingCommodityType;//商品状态 退货状态 1.正常。 2.发起退货处理中。 3.退货成功

}
