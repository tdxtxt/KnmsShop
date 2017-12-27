package com.knms.shop.android.bean.body.orderpay;

import com.google.gson.annotations.SerializedName;

/**
 * 订单列表信息
 */

public class OrderPayBody<T> {
    @SerializedName("code")
    public String code;
    @SerializedName("message")
    public String message;
    @SerializedName("globalData")
    public T globalData;
    public boolean isSuccess() {
        return "1".equals(code);
    }
}
