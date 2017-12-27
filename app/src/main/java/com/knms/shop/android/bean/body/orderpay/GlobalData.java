package com.knms.shop.android.bean.body.orderpay;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 654654 on 2017/8/22.
 */

public class GlobalData {
    @SerializedName("orderTradingBos")
    public List<OrderTradingBos> orderTradingBos;//订单数据
}
