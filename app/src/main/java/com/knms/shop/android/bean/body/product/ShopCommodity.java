package com.knms.shop.android.bean.body.product;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/3/27.
 */

public class ShopCommodity {
    @SerializedName("goid")
    public String goid;//商品id
    @SerializedName("coInspirationPic")
    public String coInspirationPic;//商品主图
    @SerializedName("browseNumber")
    public String browseNumber;//浏览量
    @SerializedName("collectNumber")
    public String collectNumber;
    @SerializedName("gooriginal")
    public String gooriginal;//商品原价
    @SerializedName("goprice")
    public String goprice;//商品现价
    @SerializedName("cotitle")
    public String cotitle;//标题
    @SerializedName("goisrecommend")
    public int goisrecommend;//是否特价（1是，0否）
    @SerializedName("iscollectNumber")
    public int iscollectNumber;//是否收藏（0表示收藏，1表示没有收藏 ）
    public String gotype;//宝贝类型：0展示商品, 6表示商城商品
}
