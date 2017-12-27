package com.knms.shop.android.bean.body.orderpay;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 654654 on 2017/8/23.
 */

public class OrderPayDetailData {

    public static final String TOASTLOACKMSG = "该订单有商品正在退款，请退款完成后再进行操作";

    @SerializedName("orderTradingBo")
    public OrderTradingBo orderTradingBo;

    public class OrderTradingBo {
        @SerializedName("tradingId")
        public String tradingId;//订单ID, 为null 或不存在 标示订单不纯在，如果存在 表示当前订单存在，此ID 需要用在订单 创建 接口上
        @SerializedName("tradingStatusTitle")
        public String tradingStatusTitle;//	交易状态 比如 ，待送货、交易关闭，等
        @SerializedName("tradingStatus")
        public String tradingStatus;//交易状态 标记码 100、200、300等
        @SerializedName("mailingName")
        public String mailingName;//收货 人姓名
        @SerializedName("mailingPhone")
        public String mailingPhone;//收货 人电话
        @SerializedName("deliveryStatus")
        public String deliveryStatus;//
        @SerializedName("mailingAddress")
        public String mailingAddress;//	收货 人地址
        @SerializedName("shopIsHide")
        public String shopIsHide;//是否隐藏商家信息 1 正常 2 隐藏 ：不会有 shopId 传入，APP 不提供跳转功能
        @SerializedName("shopId")
        public String shopId;//商家ID
        @SerializedName("shopName")
        public String shopName;//商家名
        @SerializedName("tradingTransportMoneyTitle")
        public String tradingTransportMoneyTitle;//实际运费 展示金额 或标题，（1000、待协商、免运费） 展示字符
        @SerializedName("actualMoney")
        public double actualMoney;//订单 实际付款金额 展示字符
        @SerializedName("effectiveNumber")
        public int effectiveNumber;//商品有效数量量 小计数量
        @SerializedName("userId")
        public String userId;//用户ID
        @SerializedName("buyerRemarks")
        public String buyerRemarks;//买家备注
        @SerializedName("orderCountdown")
        public int orderCountdown;//等待付款 倒计时 秒
        @SerializedName("tradingSerialid")
        public String tradingSerialid;//订单流水号 ，展示号
        @SerializedName("payOrderId")
        public String payOrderId;//支付订单号
        @SerializedName("payTypeTitle")
        public String payTypeTitle;//支付方式 微信 支付宝
        @SerializedName("createTime")
        public String createTime;//订单创建时间
        @SerializedName("userAppraise")
        public String userAppraise;//0 用户未评价 1用户已评价
        @SerializedName("businessmenAppraise")
        public String businessmenAppraise;//0 商户未回复 1 商户已未回复
        @SerializedName("isComplaint")
        public int isComplaint;//1表示没有投诉，2表示有投诉
        @SerializedName("orderType")
        public String orderType;//订单类型 1：实体 ，2 虚拟
        @SerializedName("complaintCount")
        public int complaintCount;//等于零表示没有投诉,大于零表示有投诉
        @SerializedName("usid")
        public String usid;//聊天id
        @SerializedName("tradingLocking")
        public String tradingLocking;//订单锁定(1锁定 0未锁定)
        @SerializedName("orderTradingCommoditys")
        public List<OrderTradingCommoditys> orderTradingCommoditys;
    }
}
