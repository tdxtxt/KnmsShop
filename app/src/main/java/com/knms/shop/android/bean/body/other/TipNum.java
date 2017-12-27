package com.knms.shop.android.bean.body.other;


import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/5/5.
 * 小红点提示数量
 */

public class TipNum {
    @SerializedName("a")
    public int waitGiveCount;
    @SerializedName("b")
    public int waitReceiptCount;
    @SerializedName("c")
    public int waitCommentCount;
    @SerializedName("d")
    public int waitReplyCount;

    public int e;//商城订单待付款
    public int f;//商城订单待送货
    public int g;//商城订单待收货
    public int h;//商城订单待评价
    public int i;//商城订单待待回复

    public boolean isTotalZero(){
        return this.waitCommentCount + this.waitReceiptCount + this.waitGiveCount + this.waitReplyCount
                + e + f + g + h + i == 0;
    }
}
