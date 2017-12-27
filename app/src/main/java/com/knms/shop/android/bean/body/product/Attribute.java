package com.knms.shop.android.bean.body.product;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 654654 on 2017/9/7.
 */

public class Attribute {
    @SerializedName(value = "attributeKey",alternate = {"paramName","key"})
    public String name;
    @SerializedName(value = "attributeValue",alternate = {"paramValue","value"})
    public String value;
    @SerializedName(value = "attributeSort",alternate = {"weight","sorting"})
    public int weight;
}
