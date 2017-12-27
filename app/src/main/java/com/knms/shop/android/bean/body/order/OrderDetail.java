package com.knms.shop.android.bean.body.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 654654 on 2017/5/2.
 */

public class OrderDetail {
    @SerializedName("orid")
    public String orid;//订单id
    @SerializedName("orcontractno")
    public String orcontractno;//合同号
    @SerializedName("orcreated")
    public String orcreated;//订货时间
    @SerializedName("ordeliverytime")
    public String ordeliverytime;//送货时间
    @SerializedName("orreceivercontacts")
    public String orreceivercontacts;//订货人
    @SerializedName("orreceiverphone")
    public String orreceiverphone;//联系电话
    @SerializedName("ordeliverylocation")
    public String ordeliverylocation;//送货地址
    @SerializedName("orcontractamount")
    public String orcontractamount;//合同金额
    @SerializedName("oramountpaid")
    public String oramountpaid;//已付金额
    @SerializedName("preferamount")
    public String preferamount;//优惠金额
    @SerializedName("orrefundamount")
    public String orrefundamount;//退款金额
    public List<CouponList> couponList;//优惠券列表

    public class CouponList{
        public String id	;//	优惠券id(用户领取)
        public String title	;//	优惠券名称
        public String money	;//	金额（现金券有值）
        public String quantity	;//	数量（现金券有值）
        public String startTime	;//	开始时间
        public String endTime	;//	结束时间
        public String explain	;//	描述
        public String qrcode	;//	二维码编号
        public String discount	;//	折扣（折扣券有值）
        public String invalidType	;//	1：已使用，2：已过期 （仅无效券有值）
        public String created	;//	创建时间
        public String dateGainType;//	时间购买备注
    }
}
